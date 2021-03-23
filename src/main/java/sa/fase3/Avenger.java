package sa.fase3;

import robocode.*;
import sa.Position;

import static robocode.util.Utils.*;
import static sa.Utils.*;

import java.awt.*;
import java.util.*;

public class Avenger extends TeamRobot {

    private int count = 0; // Keeps track of how long we've been searching for our target (irrelevant if avenging).
    private double gunTurnAmt; // How much to turn our gun when searching.
    private Rival trackName; // Name of the robot we're currently hunting/annihilating.
    private boolean is_killing = false; // Flag that tells us if the Avenger is annihilating a robot.
    private int dist = 50; // Distance to move when hit by bullet.
    private Map<String, Position> teammates = new HashMap<>(); // Name of teammates and respective positions.

    public void run() {

        // Set colors of Avenger class robot.
        this.setBodyColor(new Color(0, 0, 0));
        this.setGunColor(new Color(210, 0, 0));
        this.setRadarColor(new Color(255, 255, 255));
        this.setScanColor(Color.black);
        this.setBulletColor(Color.red);

        // Inform teammates of Avenger's name and position.
        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }

        System.out.println("My teammates are: " + this.teammates.toString());

        this.trackName = null; // Initialize to not tracking anyone.
        this.setAdjustGunForRobotTurn(true); // Make gun and radar turn independent from Avenger turning.
        this.gunTurnAmt = 20; // Initialize gunTurn to 20.

        System.out.println("Avenger name is " + this.getName());

        // Hunt for given enemy.
        while (true) {
            this.hunting();
        }
    }

    // Avenger's behaviour function for chasing his prey.
    public void hunting()
    {
        // turn the Gun (looks for enemy).
        this.turnGunRight(this.gunTurnAmt);
        // Keep track of how long we've been looking.
        this.count++;
        // If we've haven't seen our target for 2 turns, look left.
        if (this.count > 2) {
            this.gunTurnAmt = -20;
        }
        // If we still haven't seen our target for 5 turns, look right.
        if (this.count > 5) {
            this.gunTurnAmt = 20;
        }
        // If we *still* haven't seen our target after 10 turns, find another target unless given order to avenge.
        if (this.count > 11) {
            // If it is avenging it keeps searching instead of choosing another target.
            if(!this.is_killing)
                this.trackName = null;
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {

        // Act only if it scans an enemy
        if(!this.teammates.containsKey(e.getName())){

            // If we have a target, and this isn't it, return immediately
            // so we can get more ScannedRobotEvents.
            if ((this.trackName != null && !e.getName().equals(this.trackName.getName()))) {
                return;
            }

            // If we don't have a prey, this robot becomes it.
            if (this.trackName == null) {
                this.trackName = new Rival(e, findPosition(this,e));
                System.out.println("Hunting " + this.trackName.getName());
            }
            // This is our target, start counting from 0 to prepare the hunt.
            this.count = 0;
            // If our target is too far away, turn and move toward it.
            if (e.getDistance() > 150) {
                this.gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (this.getHeading() - this.getRadarHeading()));

                this.turnGunRight(this.gunTurnAmt);
                this.turnRight(e.getBearing());
                this.ahead(e.getDistance() - 140);
                return;
            }

            // If our target is close.
            this.gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (this.getHeading() - this.getRadarHeading()));
            this.turnGunRight(this.gunTurnAmt);
            this.fire(3);

            // If our target is too close, back up.
            if (e.getDistance() < 100) {
                if (e.getBearing() > -90 && e.getBearing() <= 90) {
                    this.back(40);
                } else {
                    this.ahead(40);
                }
            }

            // Run method onScannedRobot from the start.
            this.scan();
        }

    }

    // What to do when a robot dies.
    public void onRobotDeath(RobotDeathEvent e){
        // if prey who is being annihilated is killed, be available to annihilate (receive new order of avenging)
        // or scan new prey itself.
        if(this.trackName != null && e.getName().equals(this.trackName.getName()) && this.is_killing){
            this.is_killing = false;
            this.trackName = null;

            this.scan();
        }
    }

    // What to do when hit by robot.
    public void onHitRobot(HitRobotEvent e) {

        // Set colliding robot as the new prey, unless avenging or already hunting him.
        if(!this.is_killing && this.trackName != null && this.trackName.getName().equals(e.getName())){
            this.trackName = new Rival(e);

            this.gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (this.getHeading() - this.getRadarHeading()));
            this.turnGunRight(this.gunTurnAmt);
            this.fire(10);
        }

        double bearing = e.getBearing();
        this.turnRight(-bearing);
        this.ahead(50);

        this.scan();
    }
    // What to do when it hits a wall.
    public void onHitWall(HitWallEvent e) {
        double bearing = e.getBearing();
        this.turnRight(-bearing);
    }

    // What to do when it receives a message.
    public void onMessageReceived(MessageEvent e) {

        Message message = (Message) e.getMessage();

        switch (message.getType()) {
            // If it receives an order of avenging from Boss.
            case Message.ATTACK:
                // Only accepts order if it's not avenging.
                if(!this.is_killing){
                    this.trackName = message.getRival();
                    System.out.println("Annihilation order for robot: " + this.trackName.getName() + ".");
                    this.is_killing = true;
                }
                else System.out.println("Unable to accept order, already avenging.");
                break;
            // If it receives names and positions of teammates.
            case Message.INFO:
                if(message.getType() == Message.INFO){
                    this.teammates.put(message.getSender(), message.getPosition());
                }
                break;
        }
    }
}