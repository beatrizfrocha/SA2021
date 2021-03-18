package sa.fase1;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.*;

import standardOdometer.Odometer;

public class SmartRobot extends AdvancedRobot {

    private boolean is_racing = false;
    private boolean scanned = false;

    private MyOdometer myOdometer = new MyOdometer("MyOdometer", this);
    private Odometer odometer = new Odometer("IsRacing", this);
    private int robots_scanned = 0;

    public void run() {
        this.addCustomEvent(this.myOdometer);

        // Go to the starting position
        this.move(18, 18);

        // Wait until the 3 rockquads are in their correct positions
        for (int i = 0; i < 160; i++) {
            this.doNothing();
        }

        this.addCustomEvent(this.odometer);

        // Start the race
        this.myOdometer.start_race();
        this.is_racing = true;

        // Point to north
        this.turnRight(360 - this.getHeading());

        // Scan 3 times 3 different obstacles
        while (this.robots_scanned < 3)
            if (!this.scanned) this.turnRadarRight(45);

        // Go back to the starting position
        this.move(18, 18);

        System.out.println("Distance travelled -> " + String.format("%.2f", this.myOdometer.stop_race()));
    }

    public void move(double xf, double yf) {
        double xi = this.getX();
        double yi = this.getY();
        double distance = euclideanDistance(xi, yi, xf, yf);

        double angle = angleBetween(xi, yi, xf, yf);

        // Angle that the robot has to turn to be aligned with the desired position
        angle = 180 - angle;

        this.turnLeft(normalRelativeAngleDegrees(angle + this.getHeading()));
        this.ahead(distance);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (this.is_racing && !this.scanned) {
            this.scanned = true;

            // Place the radar in its normal direction
            this.turnRadarLeft(45);

            // Turn robot and point it to the target
            this.turnRight(e.getBearing());

            // Move in the rockquad's direction and stop before hitting it
            this.ahead(e.getDistance() - 50);

            // Outline the robot
            this.bypass_obstacle();

            this.scanned = false;
            this.robots_scanned++;
        }
    }

    public void bypass_obstacle() {
        this.turnLeft(90);

        for (int i = 0; i < 53; i++) {
            this.ahead(3);
            this.turnRight(3.375);
        }

        this.turnRadarLeft(45);
    }

    // If it collides with another robot while going to the starting position, it goes back and tries again
    public void onHitRobot(HitRobotEvent e) {
        if (!this.is_racing) {
            this.back(50);
            this.turnRight(45);
            this.ahead(60);
            this.move(18, 18);
        }
    }

    public void onStatus(StatusEvent event) {
        if (event == null || event.getStatus() == null) {
            System.out.println("Invalid event");
        }
        this.myOdometer.calculateDistanceTravelled();
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("MyOdometer")) {
            this.myOdometer.calculateDistanceTravelled();
        }
        if (cd.getName().equals("IsRacing")) {
            this.odometer.getRaceDistance();
        }
    }
}
