package sa;

import robocode.*;
import standardOdometer.Odometer;

public class MyRobot extends AdvancedRobot {
    private Odometer odometer = new Odometer("isRacing", this);
    private MyOdometer myOdometer = new MyOdometer("MyOdometer", this);

    public void run() {
        addCustomEvent(odometer);
        addCustomEvent(myOdometer);
        this.myOdometer.start_race();
        ahead(20);
        back(20);
        double result = this.myOdometer.stop_race();
        System.out.println("Distance travelled -> " + String.format("%.2f", result));
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
}