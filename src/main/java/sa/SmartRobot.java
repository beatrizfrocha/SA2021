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
    private MyOdometer myOdometer = new MyOdometer("MyOdometer", this);

    public void run() {
        addCustomEvent(this.odometer);
        addCustomEvent(this.myOdometer);
        this.myOdometer.start_race();

        // Ir para o canto inferior esquerdo
        move(18,18);

        System.out.println("Distance travelled -> " + String.format("%.2f", this.myOdometer.stop_race()));
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("MyOdometer")) {
            this.myOdometer.calculateDistanceTravelled();
        }
        if (cd.getName().equals("isRacing")) {
            this.odometer.getRaceDistance();
        }
    }

    public void onStatus(StatusEvent event) {
        this.myOdometer.calculateDistanceTravelled();
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
            move(18,18);
        }
    }
}
