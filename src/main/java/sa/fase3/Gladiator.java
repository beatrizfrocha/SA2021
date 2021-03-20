package sa.fase3;

import robocode.*;
import sa.Position;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static robocode.Rules.MAX_BULLET_POWER;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.informPosition;
import static sa.Utils.normalizeBearing;

public class Gladiator extends TeamRobot implements Droid {

    private Rival rival;
    private boolean shooting = false;
    private boolean boss_dead = false;
    private boolean saviour_dead = false;
    private boolean battlefield_mode = false;
    private boolean peek = false;
    private int direction = 1;
    private double moveAmount;
    private int dist = 50; // distance to move when we're hit
    private Map<String, Position> teammates = new HashMap<>();

    public void run() {
        setBodyColor(new Color(65, 252, 3));
        setGunColor(new Color(65, 252, 3));
        setRadarColor(new Color(65, 252, 3));

        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }

        System.out.println("My team is " + teammates.toString());
        this.setAdjustGunForRobotTurn(true);

        while(true){
            // Tell the game that when we take move,
            // we'll also want to turn right... a lot.
            this.setTurnRight(10000);
            // Limit our speed to 5
            this.setMaxVelocity(5);
            // Start moving (and turning)
            this.ahead(10000);
            // Repeat.
        }

    }

    public void onMessageReceived(MessageEvent evnt) {
        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()) {
            case Message.REQUEST:
                informPosition(this);
                break;
            case Message.ATTACK:
                this.rival = msg.getRival();
                this.shooting = true;
                this.shoot_rival(this.rival);
                break;
            case Message.INFO:
                teammates.put(msg.getSender(), msg.getPosition());
                break;
        }
    }

    public void shoot_rival(Rival rival) {

        double posX = rival.getX() - this.getX();
        double posY = rival.getY() - this.getY();

        double angle = Math.toDegrees(Math.atan2(posX, posY));
        double gun_angle = normalRelativeAngleDegrees(angle - this.getGunHeading());

        this.turnGunRight(gun_angle);
        this.fire(MAX_BULLET_POWER);

    }

    public void onHitRobot(HitRobotEvent evnt) {
        if (this.teammates.containsKey(evnt.getName()) && !battlefield_mode) {
            double bearing = evnt.getBearing();
            this.turnRight(-bearing);
            this.ahead(50);
        } if(!this.teammates.containsKey(evnt.getName()) && !battlefield_mode) {
            if (evnt.getBearing() > -10 && evnt.getBearing() < 10) {
                fire(3);
            }
        }
        else {
            // If he's in front of us, set back up a bit.
            if (evnt.getBearing() > -90 && evnt.getBearing() < 90) {
                back(100);
            } // else he's in back of us, so set ahead a bit.
            else {
                ahead(100);
            }
        }
    }

    public void onRobotDeath(RobotDeathEvent evnt) {
        String name = evnt.getName();
        System.out.println("My rival is = " + this.rival.getName());
        System.out.println("This robot died = " + name);
        if (this.rival != null && name.equals(this.rival.getName())) {
            this.rival.reconfigure();
            this.shooting = false;
        }
        if (name.contains("Boss")) {
            this.boss_dead = true;
            if (this.saviour_dead) {
                this.battlefield_mode = true;
                this.goAroundBattlefield();
            }
        }
        if (name.contains("Saviour")) {
            this.saviour_dead = true;
            if (this.boss_dead) {
                this.battlefield_mode = true;
                this.goAroundBattlefield();
            }
        }
    }

    public void goAroundBattlefield() {
        // Initialize moveAmount to the maximum possible for this battlefield.
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        // Initialize peek to false
        peek = false;

        // turnLeft to face a wall.
        // getHeading() % 90 means the remainder of
        // getHeading() divided by 90.
        turnLeft(getHeading() % 90);
        ahead(moveAmount);
        // Turn the gun to turn right 90 degrees.
        peek = true;
        turnGunRight(90);
        turnRight(90);

        while (true) {
            // Look before we turn when ahead() completes.
            peek = true;
            if(this.getTime()%5==0){
                this.direction*=-1;
            }
            // Move up the wall
            ahead(moveAmount);
            // Don't look now
            peek = false;
            // Turn to the next wall
            turnRight(90*this.direction);
        }
    }
}

