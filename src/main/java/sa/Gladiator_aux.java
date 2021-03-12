package sa;

import java.io.IOException;
import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.*;

public class Gladiator_aux extends TeamRobot {

    public void run() {

        System.out.println("Pos inicial: " + this.getX() + "," + this.getY());
        move(400,300);

        for (int i = 0; i < 60; i++) {
            this.doNothing();
        }
        Message msg = new Message(Message.SHOOT,18,18);
        Message msg2 = new Message(Message.TURN);
        Message msg3 = new Message(Message.MOVE,400,300);

        try {
            broadcastMessage(msg);
            for (int i = 0; i < 60; i++) {
                this.doNothing();
            }
            broadcastMessage(msg2);
            for (int i = 0; i < 60; i++) {
                this.doNothing();
            }
            broadcastMessage(msg3);
        } catch (IOException e) {
            e.printStackTrace();
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
        this.ahead(distance);
    }


}
