package sa.fase2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import robocode.*;
import sa.Position;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.euclideanDistance;

public class Shepherd extends TeamRobot {

    private final List<String> new_teammates = new ArrayList<>();
    private boolean moving = true;
    private int hits = 0;

    public void run() {

        String[] teammates = this.getTeammates();

        if (teammates != null) {
            for(String member: teammates) {
                String[] parts = member.split(" ");
                int num = Integer.parseInt(parts[1].substring(1, parts[1].length() - 1));
                num++;

                int count = 0;
                // check if there are repeated elements from parts[0] in array teammates
                for(String t: teammates)
                    if(t.contains(parts[0]))
                        count++;

                String newVersion;
                if(count > 1)
                    newVersion = parts[0] + " " + "(" + num + ")";
                else
                    newVersion = parts[0];

                this.new_teammates.add(newVersion);
            }
        }

        this.move(18,18);
        this.moving = false;

        for (int i = 0; i < 60; i++) {
            this.doNothing();
        }

        this.dance();

        for (int i = 0; i < 120; i++) {
            this.doNothing();
        }

        this.move_to_position(400,300);

        for (int i = 0; i < 500; i++) {
            this.doNothing();
        }

        this.move_to_corner();

        for (int i = 0; i < 300; i++) {
            this.doNothing();
        }

        // Rotate the radar infinitely
        this.turnRadarLeftRadians(Double.POSITIVE_INFINITY);
    }

    // ---------------------- Events ----------------------

    public void onScannedRobot(ScannedRobotEvent e) {

        if (!this.moving && !this.new_teammates.contains(e.getName())) {
            double angleToEnemy = e.getBearing();

            // Calculate the angle to the scanned robot
            double angle = Math.toRadians((this.getHeading() + angleToEnemy % 360));

            // Calculate the coordinates of the robot
            double enemyX = this.getX() + Math.sin(angle) * e.getDistance();
            double enemyY = this.getY() + Math.cos(angle) * e.getDistance();

            this.attack(enemyX,enemyY);
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        if(this.hits<5){
            this.back(50);
            this.turnLeft(45);
            this.ahead(60);
            this.move(18,18);
            this.hits++;
        }
        else{
            this.back(50);
            this.turnRight(45);
            this.ahead(60);
            this.move(18,18);

        }

    }

    // ---------------------- Comunication ----------------------

    public void attack(double x, double y){
        Message msg = new Message(Message.SHOOT,new Position(x,y));
        try {
            this.broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dance(){
        Message msg = new Message(Message.SPIN);
        try {
            this.broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void move_to_position(double x, double y){
        Message msg = new Message(Message.MOVE,new Position(x,y));
        try {
            this.broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void move_to_corner(){
        double w = this.getBattleFieldWidth();
        double h = this.getBattleFieldHeight();
        Message msg1 = new Message(Message.MOVE,new Position(18+50,18+50));
        Message msg2 = new Message(Message.MOVE,new Position(w-18,18));
        Message msg3 = new Message(Message.MOVE,new Position(18,h-18));
        Message msg4 = new Message(Message.MOVE,new Position(w-18,h-18));
        Message[] msgs = new Message[]{msg1,msg2,msg3,msg4};
        try {
            for(int i = 1; i < this.new_teammates.size(); i++){
                this.sendMessage(this.new_teammates.get(i),msgs[i-1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------- Movement ----------------------

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