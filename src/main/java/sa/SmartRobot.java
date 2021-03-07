package sa;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.distanciaEntrePontos;

import standardOdometer.Odometer;

public class SmartRobot extends AdvancedRobot {

    private boolean is_racing = false;
    private boolean finished = false;
    private boolean scanned = false;

    private Odometer odometer = new Odometer("isRacing", this);
    private MyOdometer roundOdometer = new MyOdometer("MyOdometer", this);

    public void run() {
        addCustomEvent(this.odometer);
        addCustomEvent(this.roundOdometer);

        // Ir para o canto inferior esquerdo
        move(18,18);

        this.roundOdometer.start_race();
        is_racing = true;

        // Espera antes de se mover para posição inicial
        for (int i = 0; i < 160 ; i++){
            doNothing();
        }

        // Aponta o carro para cima
        turnRight(360-getHeading());

        int j = 0;
        while(j<3) { // Scan 3 vezes pois são 3 robôs
            if(!scanned) {
                // Scanning no sentido dos ponteiros do relógio
                turnRadarRight(45);
                j++;
            }
        }

        move(18,18);

        System.out.println("Distância percorrida = " + String.format("%.2f", this.roundOdometer.stop_race()));
    }

    public void onStatus(StatusEvent event){
        if(event == null || event.getStatus() == null){
            System.out.println("Evento Inválido");
            return ;
        }
        if(roundOdometer != null){
            roundOdometer.calculateDistanceTravelled();
        }

    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("MyOdometer")) {
            this.roundOdometer.calculateDistanceTravelled();
        }
        if (cd.getName().equals("isRacing")) {
            this.odometer.getRaceDistance();
        }
    }

    public void move(double xf, double yf){
        double xi = getX();
        double yi = getY();
        double distance = distanciaEntrePontos(xi, yi, xf, yf);

        double angle = angleBetween(xi, yi, xf, yf);

        // Conversão do ângulo da perspetiva dentro do triângulo para a do robot
        angle = 180-angle;

        turnLeft(normalRelativeAngleDegrees(angle + getHeading()));
        ahead(distance);
    }

    // Se colidir com outro robot enquanto está a ir para a posição inicial, recua e tenta de novo.
    public void onHitRobot(HitRobotEvent e){
        if(!is_racing){
            back(50);
            turnLeft(45);
            ahead(50);
            move(18,18);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(is_racing && !scanned){
            scanned = true;

            // Posicionar o radar corretamente
            turnRadarLeft(45);

            // Ângulo entre a direção do SmartRobot e do robô alvo
            double degreesToTurn = e.getBearing();

            // Virar SmartRobot de modo a apontar para robô alvo
            turnRight(degreesToTurn);

            // Mover até robô e parar antes de bater
            ahead(e.getDistance()-50);

            // Contorna robô
            goAroundRobot();

            scanned = false;
        }
    }

    public void goAroundRobot(){
        turnLeft(90);

        // Começar os turnos
        for(int i=0;i<40;i++){
                ahead(2.99956);
                turnRight(3.396);
        }

        ahead(10);

    }
}
