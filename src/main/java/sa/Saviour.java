package sa;

import robocode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.move;

public class Saviour extends TeamRobot {

    boolean amILeader = false;
    // For when Saviour becomes the leader
    private final List<String> new_teammates = new ArrayList<>();

    Rival target;
    Map<String, Rival> enemies = new HashMap<>();
    int aliveEnemies = 4;
    int enemiesToScan = aliveEnemies;
    int scannedEnemies = 0;

    public void run() {

        for (int i = 0; i < 30; i++) {
            this.doNothing();
        }
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
        else System.out.println("Nao tenho teammates");

        this.setAdjustGunForRobotTurn(true);
        // Loop forever
        while (true) {
            turnGunRight(10); // Scans automatically
        }
    }

    public void onMessageReceived(MessageEvent evnt) {
        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()) {
            case Message.INFO:
                move(msg.getX(),msg.getY(),this);
                break;
        }
    }

    /**
     * onScannedRobot:  We have a target.  Go get it.
     */
    public void onScannedRobot(ScannedRobotEvent e) {

        if(!this.new_teammates.contains(e.getName())) {

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

