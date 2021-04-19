package sa;

import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import sa.fase3.Message;

import java.io.IOException;

public class Utils {

    public static double euclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    public static double angleBetween(double xi, double yi, double xf, double yf) {

        double hypotenuse = euclideanDistance(xi, yi, xf, yf);
        double opposite = euclideanDistance(xi, yf, xf, yf);
        double sine = opposite / hypotenuse;

        // Calculate the angle based on Pythagoras theorem
        double angle = Math.asin(sine);

        // Radians to degrees
        return (180 / Math.PI) * angle;

    }

    public static double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public static Position findPosition(TeamRobot r, ScannedRobotEvent e){
        double angleToEnemy = e.getBearing();

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((r.getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = r.getX() + Math.sin(angle) * e.getDistance();
        double enemyY = r.getY() + Math.cos(angle) * e.getDistance();

        return new Position(enemyX,enemyY);
    }

    public static void informPosition(TeamRobot t) {
        Message msg = new Message(t.getName(), Message.INFO, new Position(t.getX(), t.getY()));
        try {
            t.broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
