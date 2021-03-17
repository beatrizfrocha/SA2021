package sa;

import robocode.ScannedRobotEvent;

public class Rival {

    private double bearing;
    private double distance;
    private double energy;
    private double heading;
    private double velocity;
    private String name;
    private Position p;

    public Rival() {
        this.bearing = 0;
        this.distance = 0;
        this.energy = 0;
        this.heading = 0;
        this.velocity = 0;
        this.name = null;
        this.p = null;
    }

    public Rival(ScannedRobotEvent e, Position p){
        this.bearing = e.getBearing();
        this.distance = e.getDistance();
        this.energy = e.getEnergy();
        this.heading = e.getHeading();
        this.velocity = e.getVelocity();
        this.name = e.getName();
        this.p = p;
    }

    public double getBearing() {
        return this.bearing;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getEnergy() {
        return this.energy;
    }

    public double getHeading() {
        return this.heading;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public String getName() {
        return this.name;
    }

    public double getX() { return p.getX(); }

    public double getY() { return p.getY(); }

    public void reconfigure() {
        this.bearing = 0;
        this.distance = 0;
        this.energy = 0;
        this.heading = 0;
        this.velocity = 0;
        this.name = null;
        this.p = null;
    }

    @Override
    public String toString() {
        return "Rival{" +
                "bearing=" + bearing +
                ", distance=" + distance +
                ", energy=" + energy +
                ", heading=" + heading +
                ", velocity=" + velocity +
                ", name='" + name + '\'' +
                ", p=" + p.toString() +
                '}';
    }
}
