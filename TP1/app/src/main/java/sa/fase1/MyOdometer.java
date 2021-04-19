package sa.fase1;

import robocode.AdvancedRobot;
import robocode.Condition;

import static sa.Utils.euclideanDistance;

public class MyOdometer extends Condition {
    private boolean is_racing;
    private boolean finished;
    private double distance_travelled = 0;
    private String name;
    private AdvancedRobot r;
    private double oldX;
    private double oldY;
    private double x;
    private double y;

    public MyOdometer(String name, AdvancedRobot r) {
        this.name = name;
        this.r = r;
    }

    public void start_race() {
        this.is_racing = true;
        this.finished = false;
        this.oldX = r.getX();
        this.oldY = r.getY();
        this.x = r.getX();
        this.y = r.getY();
    }

    public boolean test() {
        this.r.setDebugProperty("is_racing", String.valueOf(this.is_racing));
        this.r.setDebugProperty("finished", String.valueOf(this.finished));
        this.r.setDebugProperty("distance_travelled", String.valueOf(String.format("%.2f", this.distance_travelled)));
        return r.getTime() != 0;
    }

    public void calculateDistanceTravelled() {
        if (is_racing) {
            this.x = r.getX();
            this.y = r.getY();
            this.distance_travelled += euclideanDistance(this.x, this.y, this.oldX, this.oldY);
            this.oldX = this.x;
            this.oldY = this.y;
        }
    }

    public double stop_race() {
        this.is_racing = false;
        this.finished = true;
        return this.distance_travelled;
    }
}