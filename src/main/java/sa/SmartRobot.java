package sa;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.distanciaEntrePontos;

import standardOdometer.Odometer;

public class SmartRobot extends AdvancedRobot {

    // Ver se vale a pena ter estas 2 vars
    private boolean is_racing = false;
    private boolean finished = false;

    private Odometer odometer = new Odometer("isRacing", this);
    private MyOdometer roundOdometer = new MyOdometer("MyOdometer", this);
    private boolean robotScanned = false;

    public void run() {
        addCustomEvent(this.odometer);
        addCustomEvent(this.roundOdometer);

        // Ir para o canto inferior esquerdo
        move(18,18);

        this.roundOdometer.start_race();
        is_racing = true;

        // Aponta o carro para cima
        turnRight(360-getRadarHeading());

        int j = 0;
        while(j<3) { // 3 robots => 3 scans
            if(!robotScanned) {
                // Scanning to the right side
                turnRadarRight(45);
                j++;
            }
        }

        move(18,18);

        System.out.println("Distance travelled -> " + String.format("%.2f", this.roundOdometer.stop_race()));
    }

    public void onStatus(StatusEvent event){
        if(event == null || event.getStatus() == null){
            System.out.println("Null Event or Status");
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
            turnRight(45);
            ahead(50);
            move(18,18);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(is_racing && !robotScanned){
            robotScanned = true;

            // Place the radar normally
            turnRadarLeft(45);

            // Angle to the next robot
            double degreesToTurn = e.getBearing();

            // Turn to the next robot
            turnRight(degreesToTurn);

            // Go ahead and stop just before
            ahead(e.getDistance()-50);

            // Go around the robot
            goAroundRobot();

            robotScanned = false;
        }
    }

    public void goAroundRobot(){
        turnLeft(90);

        // Start turns
        for(int i=0;i<48;i++){
            ahead(2.5);
            turnRight(2.8125);
        }

        // Last turn more safely (for the radar catch the next robot)
        ahead(10);
        turnRight(15);
    }
}
