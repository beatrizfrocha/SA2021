package sa.fase2;

import robocode.*;

import static robocode.Rules.MAX_BULLET_POWER;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.euclideanDistance;


public class Sheep extends TeamRobot implements Droid{

    private int tries = 0;
    private double x;
    private double y;
    private boolean is_moving = false;

    public void run() {
        this.setAdjustGunForRobotTurn(true);
    }
    public void onMessageReceived(MessageEvent evnt) {

        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()) {
            case Message.SHOOT:
                this.shoot(msg.getX(), msg.getY());
                break;
            case Message.MOVE:
                this.is_moving = true;
                this.x = msg.getX();
                this.y = msg.getY();
                this.move(msg.getX(), msg.getY());
                break;
            case Message.SPIN:
                this.turnLeft(720);
                break;
        }
    }

    public void move(double xf, double yf) {
        double xi = this.getX();
        double yi = this.getY();
        double distance = euclideanDistance(xi, yi, xf, yf);

        double angle = angleBetween(xi, yi, xf, yf);

        // Angle that the robot has to turn to be aligned with the desired position
        if(xi > xf && yi > yf)
            angle = 90 + (90-angle);
        if(xi < xf && yi < yf)
            angle = 270 + (90-angle);
        if(xi < xf && yi > yf)
            angle = 270 - (90-angle);
        if(xi > xf && yi < yf)
            angle = 90 - (90-angle);

        this.turnLeft(normalRelativeAngleDegrees(angle + this.getHeading()));
        this.ahead(distance-50);
    }

    public void shoot(double x, double y) {

        double posX = x - this.getX();
        double posY = y - this.getY();

        double angle = Math.toDegrees(Math.atan2(posX, posY));
        double gun_angle = normalRelativeAngleDegrees(angle - this.getGunHeading());

        this.turnGunRight(gun_angle);
        this.fire(MAX_BULLET_POWER);

    }

    public void onHitRobot(HitRobotEvent e) {
        this.tries++;
        if(this.is_moving && euclideanDistance(this.getX(), this.getY(), this.x, this.y) > 50) {
            this.back(50);
            this.turnRight(45);
            this.ahead(60);
            this.move(this.x,this.y);
        }
    }
}