package sa.fase3;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import sa.Position;

public class Rival implements java.io.Serializable{

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

    public Rival(HitRobotEvent e){
        this.bearing = e.getBearing();
        this.distance = 0;
        this.energy = e.getEnergy();
        this.heading = 0;
        this.velocity = 0;
        this.name = e.getName();
        this.p = null;
    }

    public Rival(HitByBulletEvent e) {
        this.bearing = e.getBearing();
        this.distance = 0;
        this.energy = 0;
        this.heading = 0;
        this.velocity = 0;
        this.name = e.getName();
        this.p = null;
    }

    public void update(ScannedRobotEvent bot, Position pos) {
        this.bearing = bot.getBearing();
        this.distance = bot.getDistance();
        this.energy = bot.getEnergy();
        this.heading = bot.getHeading();
        this.velocity = bot.getVelocity();
        this.name = bot.getName();
        this.p = pos;
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

    public double getX() { return this.p.getX(); }

    public double getY() { return this.p.getY(); }

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
                "bearing=" + this.bearing +
                ", distance=" + this.distance +
                ", energy=" + this.energy +
                ", heading=" + this.heading +
                ", velocity=" + this.velocity +
                ", name='" + this.name + '\'' +
                ", p=" + this.p.toString() +
                '}';
    }
}
