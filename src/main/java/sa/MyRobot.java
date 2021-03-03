package sa;

import robocode.*;
import standardOdometer.Odometer;

public class MyRobot extends AdvancedRobot {
    private Odometer odometer = new Odometer("isRacing", this);
    private MyOdometer myOdometer = new MyOdometer("MyOdometer", this);

    public void run() {
        addCustomEvent(odometer);
        addCustomEvent(myOdometer);
        while(true){
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("isRacing"))
            this.odometer.getRaceDistance();
        if (cd.getName().equals("MyOdometer"))
            this.myOdometer.calculateDistanceTravelled();
    }
}