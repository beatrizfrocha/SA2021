package sa.fase3;

import robocode.*;
import sa.Position;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.informPosition;
import static sa.Utils.move;

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
                move(new Position(msg.getX(),msg.getY()),this);
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
            if(!this.teammates.containsKey(e.getName())) {

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
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {

        if(amILeader) {
            super.onHitByBullet(e);
        }
        else {
            turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
            ahead(dist);
            dist *= -1;
            scan();
        }
    }
}

