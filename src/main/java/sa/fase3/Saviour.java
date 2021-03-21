package sa.fase3;

import robocode.*;
import sa.Position;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.*;

public class Saviour extends Boss {

    private boolean amILeader = false;
    private int dist = 50;

    public void run() {

        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }
        System.out.println("My team is " + teammates.toString());

        setBodyColor(new Color(221, 66, 245));
        setGunColor(new Color(221, 66, 245));
        setRadarColor(new Color(221, 66, 245));

        this.setAdjustGunForRobotTurn(true);
        // Loop forever
        while (true) {
            turnGunRight(10); // Scans automatically
        }
    }

    public void onMessageReceived(MessageEvent evnt) {
        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()) {
            case Message.COME_WITH_ME:
                if(euclideanDistance(msg.getX(),msg.getY(),this.getX(),this.getY()) > 50)
                    this.move(new Position(msg.getX(),msg.getY()),50);
                break;
            case Message.INFO:
                teammates.put(msg.getSender(), msg.getPosition());
                break;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        if(amILeader)
            super.onScannedRobot(e);
        else {
            if(!teammates.containsKey(e.getName())) {

                // ---------------------------------------- Shoot ----------------------------------------

                double absoluteBearing = getHeading() + e.getBearing();
                double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

                if (Math.abs(bearingFromGun) <= 3) {
                    turnGunRight(bearingFromGun);
                    if (getGunHeat() == 0) {
                        fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
                    }
                } else {
                    turnGunRight(bearingFromGun);
                }
                if (bearingFromGun == 0) {
                    scan();
                }

                // ---------------------------------------------------------------------------------------
            }
        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if(amILeader)
            super.onRobotDeath(e);
        else {
            if (e.getName().contains("Boss")) {
                amILeader = true;
            }
            System.out.println("Robot: " + e.getName() + " caputou.");

            if (e.getName().contains("Gladiator")) {
                gladiators_alive--;
                System.out.println("gladiators_alive = " + gladiators_alive);
                if (amILeader && gladiators_alive == 0) {
                    sicko_mode = true;
                    System.out.println("Young LaFlame, he in sicko mode");
                }
            }
            teammates.remove(e.getName());
            System.out.println("current team after is " + teammates.toString());
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {

        if(amILeader) {
            super.onHitByBullet(e);
        }
    }

    public void onHitRobot(HitRobotEvent e) {

        if (e.getBearing() > -90 && e.getBearing() <= 90) {
            back(20);
        } else {
            ahead(20);
        }
        if (e.isMyFault()) {
            turnRight(10);
        }
    }

    public void move(Position p, int dist) {
        double xi = this.getX();
        double yi = this.getY();
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

        this.turnLeft(normalRelativeAngleDegrees(angle + this.getHeading()));
        this.ahead(distance-dist);
    }
}

