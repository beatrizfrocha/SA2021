package sa;

import robocode.*;

import java.io.IOException;

import static robocode.Rules.MAX_BULLET_POWER;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.euclideanDistance;


public class Gladiator2 extends TeamRobot {
    private int direction = 1;
    public void run() {
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
    }

    public void onMessageReceived(MessageEvent evnt) {

        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()) {
            case Message.SHOOT:
                this.shoot(msg.getX(), msg.getY());
                break;
            case Message.MOVE:
                this.move(msg.getX(), msg.getY());
                break;
            case Message.TURN:
                this.move(msg.getX(), msg.getY());
                break;

        }
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

    public void shoot(double x, double y) {

        double posX = x - this.getX();
        double posY = y - this.getY();

        double angle = Math.toDegrees(Math.atan2(posX, posY));
        double gun_angle = normalRelativeAngleDegrees(angle - this.getGunHeading());

        this.turnGunRight(gun_angle);
        this.fire(MAX_BULLET_POWER);


    }
}















