package sa.fase3;

import robocode.*;
import sa.Position;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static robocode.Rules.MAX_BULLET_POWER;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.informPosition;

public class Gladiator extends TeamRobot implements Droid {

    private Rival rival;
    private boolean shooting = false;
    private boolean boss_dead = false;
    private boolean saviour_dead = false;
    private boolean battlefield_mode = false;
    private boolean peek;
    private int direction = 1;
    private double moveAmount;
    private Map<String, Position> teammates = new HashMap<>();

    public void run() {
        this.setBodyColor(new Color(65, 252, 3));
        this.setGunColor(new Color(65, 252, 3));
        this.setRadarColor(new Color(65, 252, 3));

        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }

        System.out.println("My team is " + this.teammates.toString());
        this.setAdjustGunForRobotTurn(true);

        // Loop forever
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
                this.teammates.put(msg.getSender(), msg.getPosition());
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
        if (this.teammates.containsKey(evnt.getName()) && !this.battlefield_mode) {
            double bearing = evnt.getBearing();
            this.turnRight(-bearing);
            this.ahead(50);
        } if (!this.teammates.containsKey(evnt.getName()) && !this.battlefield_mode) {
            if (evnt.getBearing() > -10 && evnt.getBearing() < 10) {
                fire(MAX_BULLET_POWER);
            }
            if (evnt.isMyFault()) {
                turnRight(10);
            }
        } if(this.battlefield_mode) {
            // If he's in front of us, set back up a bit.
            if (evnt.getBearing() > -90 && evnt.getBearing() < 90) {
                this.back(100);
            } // else he's in back of us, so set ahead a bit.
            else {
                this.ahead(100);
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
        this.moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight());
        // Initialize peek to false
        this.peek = false;

        // turnLeft to face a wall.
        // getHeading() % 90 means the remainder of
        // getHeading() divided by 90.
        this.turnLeft(this.getHeading() % 90);
        this.ahead(this.moveAmount);
        // Turn the gun to turn right 90 degrees.
        this.peek = true;
        this.turnGunRight(90);
        this.turnRight(90);

        while (true) {
            // Look before we turn when ahead() completes.
            this.peek = true;
            // Move up the wall
            this.ahead(this.moveAmount);
            // Don't look now
            this.peek = false;
            // Turn to the next wall
            this.turnRight(90*this.direction);
        }
    }
}

