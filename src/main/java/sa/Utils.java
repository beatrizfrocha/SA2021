package sa;

import robocode.TeamRobot;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Utils {

    public static Map<Integer, SimpleEntry<Position,Position>> initQuadrantes(double fieldX, double fieldY){
        Map<Integer, SimpleEntry<Position,Position>> quadrantes = new HashMap<>();

        double fieldXMid = fieldX/2;
        double fieldYMid = fieldY/2;

        quadrantes.put(1, new SimpleEntry<>(new Position(fieldXMid,fieldYMid), new Position(fieldX,fieldY)));
        quadrantes.put(2, new SimpleEntry<>(new Position(0,fieldYMid), new Position(fieldXMid,fieldY)));
        quadrantes.put(3, new SimpleEntry<>(new Position(0,0), new Position(fieldXMid,fieldYMid)));
        quadrantes.put(4, new SimpleEntry<>(new Position(fieldXMid,0), new Position(fieldX,fieldYMid)));

        return quadrantes;
    }

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

    public static void move(Position p, TeamRobot robot) {
        double xi = robot.getX();
        double yi = robot.getY();
        double xf = p.getX();
        double yf = p.getY();
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

        robot.turnLeft(normalRelativeAngleDegrees(angle + robot.getHeading()));
        robot.ahead(distance);
    }

}
