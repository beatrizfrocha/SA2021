package sa.fase3;

import robocode.*;
import sa.Position;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.informPosition;
import static sa.Utils.move;

public class Saviour extends TeamRobot {

    private boolean amILeader = false;
    // For when Saviour becomes the leader
    private Map<String, Position> teammates = new HashMap<>();

    private Rival target;
    private Map<String, Rival> enemies = new HashMap<>();
    private int aliveEnemies = 4;
    private int enemiesToScan = aliveEnemies;
    private int scannedEnemies = 0;

    public void run() {

        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }
        System.out.println("My team is " + teammates.toString());

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

        if(!this.teammates.containsKey(e.getName())) {

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

        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if(e.getName().contains("Boss")){
            amILeader = true;
        }
    }
}

