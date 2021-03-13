package sa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.euclideanDistance;

public class Shepherd extends TeamRobot {

    private List<String> new_teammates = new ArrayList<>();

    public void run() {

        String[] teammates = this.getTeammates();

        if (teammates != null)
        {
            for(String member: teammates)
            {
                String[] parts = member.split(" ");
                int num = Integer.parseInt(parts[1].substring(1, parts[1].length() - 1));
                num++;

                int count = 0;
                // ver se no array teammates existem mais que 1 dos parts[0]
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

        move(18,18);

        for (int i = 0; i < 60; i++) {
            this.doNothing();
        }

        mandaDancar();

        for (int i = 0; i < 120; i++) {
            this.doNothing();
        }

        mandarMover(400,300);

        for (int i = 0; i < 500; i++) {
            this.doNothing();
        }

        mandarMoverCanto();

        for (int i = 0; i < 300; i++) {
            this.doNothing();
        }

        // Gira o radar infinitamente
        turnRadarLeftRadians(Double.POSITIVE_INFINITY);
    }

    // ---------------------- Eventos ----------------------

    public void onScannedRobot(ScannedRobotEvent e) {

        if (!new_teammates.contains(e.getName())) {
            double angleToEnemy = e.getBearing();

            // Calculate the angle to the scanned robot
            double angle = Math.toRadians((this.getHeading() + angleToEnemy % 360));

            // Calculate the coordinates of the robot
            double enemyX = this.getX() + Math.sin(angle) * e.getDistance();
            double enemyY = this.getY() + Math.cos(angle) * e.getDistance();

            mandarAtacar(enemyX,enemyY);
        }
    }

    // ---------------------- Comunicação ----------------------

    public void mandarAtacar(double x, double y){
        Message msg = new Message(Message.SHOOT,x,y);
        try {
            broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mandaDancar(){
        Message msg = new Message(Message.TURN);
        try {
            broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mandarMover(double x, double y){
        Message msg = new Message(Message.MOVE,x,y);
        try {
            broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mandarMoverCanto(){
        System.out.println("BREAK 1");
        double w = getBattleFieldWidth();
        double h = getBattleFieldHeight();
        Message msg1 = new Message(Message.MOVE,18+50,18+50);
        Message msg2 = new Message(Message.MOVE,w-18,18);
        Message msg3 = new Message(Message.MOVE,18,h-18);
        Message msg4 = new Message(Message.MOVE,w-18,h-18);
        Message[] msgs = new Message[]{msg1,msg2,msg3,msg4};
        try {
            for(int i = 1; i < this.new_teammates.size(); i++){
                sendMessage(this.new_teammates.get(i),msgs[i-1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------- Movimentação ----------------------

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
