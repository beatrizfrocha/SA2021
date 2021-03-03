package sa;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import standardOdometer.Odometer;

public class MyFirstRobot extends AdvancedRobot {

    public void run() {
        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

}
