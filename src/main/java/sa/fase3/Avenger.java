package sa.fase3;


import robocode.*;
import sa.Position;

import static robocode.util.Utils.*;
import static sa.Utils.*;

import java.awt.*;
import java.util.*;

public class Avenger extends TeamRobot {

    int count = 0; // Keeps track of how long we've been searching for our target (irrelevant if avenging).
    double gunTurnAmt; // How much to turn our gun when searching.
    Rival trackName; // Name of the robot we're currently hunting/annihilating.
    boolean is_killing = false; // Flag that tells us if the Avenger is annihilating a robot.
    int dist = 50; // Distance to move when hit by bullet.
    private Map<String, Position> teammates = new HashMap<>(); // Name of teammates and respective positions.

    public void run() {

        // Set colors of Avenger class robot.
        setBodyColor(new Color(0, 0, 0));
        setGunColor(new Color(210, 0, 0));
        setRadarColor(new Color(255, 255, 255));
        setScanColor(Color.black);
        setBulletColor(Color.red);

        // Inform teammates of Avenger's name and position.
        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }

        System.out.println("My teammates are: " + teammates.toString());

        trackName = null; // Initialize to not tracking anyone.
        setAdjustGunForRobotTurn(true); // Make gun and radar turn independent from Avenger turning.
        gunTurnAmt = 20; // Initialize gunTurn to 20.

        System.out.println("Avenger name is " + this.getName());

        // Hunt for given enemy.
        while (true) {
            hunting();
        }
    }

    // Avenger's behaviour function for chasing his target.
    public void hunting()
    {
        // turn the Gun (looks for enemy).
        turnGunRight(gunTurnAmt);
        // Keep track of how long we've been looking.
        count++;
        // If we've haven't seen our target for 2 turns, look left.
        if (count > 2) {
            gunTurnAmt = -20;
        }
        // If we still haven't seen our target for 5 turns, look right.
        if (count > 5) {
            gunTurnAmt = 20;
        }
        // If we *still* haven't seen our target after 10 turns, find another target unless given order to avenge.
        if (count > 11) {
            // If it is avenging it keeps searching instead of choosing another target.
            if(!is_killing)
                trackName = null;
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {

        // Act only if it scans an enemy
        if(!this.teammates.containsKey(e.getName())){

            // If we have a target, and this isn't it, return immediately
            // so we can get more ScannedRobotEvents.
            if ((trackName != null && !e.getName().equals(trackName.getName()))) {
                return;
            }

            // If we don't have a prey, this robot becomes it.
            if (trackName == null) {
                trackName = new Rival(e, findPosition(this,e));
                out.println("Hunting " + trackName.getName());
            }
            // This is our target, start counting from 0 to prepare the hunt.
            count = 0;
            // If our target is too far away, turn and move toward it.
            if (e.getDistance() > 130) {
                gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

                turnGunRight(gunTurnAmt);
                turnRight(e.getBearing());
                ahead(e.getDistance() - 120);
                return;
            }

            // If our target is close.
            gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
            turnGunRight(gunTurnAmt);
            fire(10);

            // If our target is too close, back up.
            if (e.getDistance() < 80) {
                if (e.getBearing() > -90 && e.getBearing() <= 90) {
                    back(40);
                } else {
                    ahead(40);
                }
            }

            // Run method onScannedRobot from the start.
            scan();
        }

    }

    // What to do when a robot dies.
    public void onRobotDeath(RobotDeathEvent e){
        // if prey who is being annihilated is killed, be available to annihilate (receive new order of avenging)
        // or scan new prey.
        if(e.getName().equals(trackName.getName()) && is_killing){
            is_killing = false;
            trackName = null;
            scan();
        }
    }

    // What to do when hit by robot.
    public void onHitRobot(HitRobotEvent e) {

        // Set colliding robot as the prey, unless avenging or already hunting him.
        if(!is_killing && trackName.getName().equals(e.getName())){
            trackName = new Rival(e);

            gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
            turnGunRight(gunTurnAmt);
            fire(10);
        }
        back(50);
    }

    // What to do when hit by a bullet.
    public void onHitByBullet(HitByBulletEvent e) {
        // When hit moves perpendicular to the bullet's direction.
        turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));

        ahead(dist);
        dist *= -1;
        scan();
    }

    // What to do when it receives a messages.
    public void onMessageReceived(MessageEvent e) {

        Message message = (Message) e.getMessage();

        switch (message.getType()) {
            // If it receives an order of avenging from Boss.
            case Message.ATTACK:
                // Only accepts order if its not avenging.
                if(!is_killing){
                    this.trackName = message.getRival();
                    System.out.println("Annihilation order for robot: " + this.trackName.getName() + ".");
                    is_killing = true;
                }
                else System.out.println("Unnable to accept order, already avenging.");
                break;
            // If it receives names and positions of teammates.
            case Message.INFO:
                if(message.getType() == Message.INFO){
                    teammates.put(message.getSender(), message.getPosition());
                }
                break;
        }
    }
}