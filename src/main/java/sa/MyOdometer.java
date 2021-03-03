package sa;

import robocode.AdvancedRobot;
import robocode.Condition;

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
        this.r.setDebugProperty("is_racing", String.valueOf(is_racing));
        this.r.setDebugProperty("finished", String.valueOf(finished));
        this.r.setDebugProperty("distance_travelled", String.valueOf(String.format("%.2f", this.distance_travelled)));
        return r.getTime() != 0;
    }

    public double euclideanDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    public void calculateDistanceTravelled(){
        if(is_racing){
            this.x = r.getX();
            this.y = r.getY();
            this.distance_travelled += euclideanDistance(this.x, this.oldX, this.y, this.oldY);
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