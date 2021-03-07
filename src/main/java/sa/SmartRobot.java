package sa;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.distanciaEntrePontos;

import standardOdometer.Odometer;

public class SmartRobot extends AdvancedRobot {

    private boolean is_racing = false;
    private boolean scanned = false;
    private final Odometer odometer = new Odometer("isRacing", this);
    private final MyOdometer roundOdometer = new MyOdometer("MyOdometer", this);
    private int robots_scanned = 0;

    public void run() {
        addCustomEvent(this.odometer);
        addCustomEvent(this.roundOdometer);

        // Ir para o canto inferior esquerdo
        move(18,18);

        // Começa a corrida
        this.roundOdometer.start_race();
        is_racing = true;

        // Espera para os Rock Quads se posicionarem corretamente
        for (int i = 0; i < 160 ; i++){
            doNothing();
        }

        // Aponta o carro para cima
        turnRight(360-getHeading());

        // Scan 3 vezes de obstáculos diferentes
        while(robots_scanned<3)
            if(!scanned) turnRadarRight(45);

        // Voltar para o canto inferior esquerdo
        move(18,18);

        System.out.println("Distância percorrida = " + String.format("%.2f", this.roundOdometer.stop_race()));
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

    public void onScannedRobot(ScannedRobotEvent e) {
        if(is_racing && !scanned){
            scanned = true;

            // Posicionar o radar corretamente
            turnRadarLeft(45);

            // Virar robot para apontar para o robot alvo
            turnRight(e.getBearing());

            // Mover até robot e parar antes de bater
            ahead(e.getDistance()-50);

            // Contorna robot
            contornaRobot();

            scanned = false;
            robots_scanned++;
        }
    }

    public void contornaRobot(){
        turnLeft(90);

        for(int i=0;i<53;i++){
            ahead(3);
            turnRight(3.375);
        }

        turnLeft(45);
        turnRadarLeft(45);
    }

    // Se colidir com outro robot enquanto está a ir para a posição inicial, recua e tenta de novo.
    public void onHitRobot(HitRobotEvent e){
        if(!is_racing){
            back(50);
            turnRight(45);
            ahead(60);
            move(18,18);
        }
    }

    public void onStatus(StatusEvent event){
        if(event == null || event.getStatus() == null){
            System.out.println("Evento Inválido");
            return ;
        }
        roundOdometer.calculateDistanceTravelled();
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
}
