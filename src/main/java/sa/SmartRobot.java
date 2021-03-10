package sa;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.*;

import standardOdometer.Odometer;

public class SmartRobot extends AdvancedRobot {

    private boolean is_racing = false;
    private boolean scanned = false;

    private final MyOdometer myOdometer = new MyOdometer("MyOdometer", this);
    private Odometer odometer = new Odometer("IsRacing", this);
    private int robots_scanned = 0;

    public void run() {
        this.addCustomEvent(this.myOdometer);

        // Ir para o canto inferior esquerdo
        this.move(18,18);

        // Espera para os Rock Quads se posicionarem corretamente
        for (int i = 0; i < 160 ; i++){
            this.doNothing();
        }

        this.addCustomEvent(this.odometer);

        // Começa a corrida
        this.myOdometer.start_race();
        this.is_racing = true;

        // Aponta o carro para cima
        this.turnRight(360-this.getHeading());

        // Scan 3 vezes de obstáculos diferentes
        while(this.robots_scanned<3)
            if(!this.scanned) this.turnRadarRight(45);

        // Voltar para o canto inferior esquerdo
        this.move(18,18);

        System.out.println("Distance travelled -> " + String.format("%.2f", this.myOdometer.stop_race()));

    }

    public void move(double xf, double yf){
        double xi = getX();
        double yi = getY();
        double distance = euclideanDistance(xi, yi, xf, yf);

        double angle = angleBetween(xi, yi, xf, yf);

        // Conversão do ângulo da perspetiva dentro do triângulo para a do robot
        angle = 180-angle;

        this.turnLeft(normalRelativeAngleDegrees(angle + this.getHeading()));
        this.ahead(distance);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(this.is_racing && !this.scanned){
            this.scanned = true;

            // Posicionar o radar corretamente
            this.turnRadarLeft(45);

            // Virar robot para apontar para o robot alvo
            this.turnRight(e.getBearing());

            // Mover até robot e parar antes de bater
            this.ahead(e.getDistance()-50);

            // Contorna robot
            this.contornaRobot();

            this.scanned = false;
            this.robots_scanned++;
        }
    }

    public void contornaRobot(){
        this.turnLeft(90);

        for(int i=0;i<53;i++){
            this.ahead(3);
            this.turnRight(3.375);
        }

        //turnLeft(45);
        this.turnRadarLeft(45);
    }

    // Se colidir com outro robot enquanto está a ir para a posição inicial, recua e tenta de novo.
    public void onHitRobot(HitRobotEvent e){
        if(!this.is_racing){
            this.back(50);
            this.turnRight(45);
            this.ahead(60);
            this.move(18,18);
        }
    }

    public void onStatus(StatusEvent event){
        if(event == null || event.getStatus() == null){
            System.out.println("Evento Inválido");
            return ;
        }
        this.myOdometer.calculateDistanceTravelled();
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("MyOdometer")) {
            this.myOdometer.calculateDistanceTravelled();
        }
        if (cd.getName().equals("IsRacing")){
            this.odometer.getRaceDistance();
        }
    }
}
