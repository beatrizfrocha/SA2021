package sa;

public class Rival {

    private double bearing;
    private double distance;
    private double energy;
    private double heading;
    private double velocity;
    private String name;
    private double x;
    private double y;

    public Rival() {
        this.bearing = 0;
        this.distance = 0;
        this.energy = 0;
        this.heading = 0;
        this.velocity = 0;
        this.name = null;
        this.x = 0;
        this.y = 0;
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

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void reconfigure() {
        this.bearing = 0;
        this.distance = 0;
        this.energy = 0;
        this.heading = 0;
        this.velocity = 0;
        this.name = null;
        this.x = 0;
        this.y = 0;
    }

}
