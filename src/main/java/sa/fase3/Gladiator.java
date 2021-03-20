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

        // When the robot hits an obstacle, it changes its direction
        if (this.getVelocity() == 0) {
            this.direction *= -1;
        }

        this.setTurnRight(normalizeBearing(rival.getBearing() + 90 - (15 * this.direction)));

        if (this.getTime() % 5 == 0) {
            this.direction *= -1;
            this.setAhead(4000 * this.direction);
        }

        this.turnGunRight(gun_angle);

        if (this.getGunHeat() == 0 && Math.abs(this.getGunTurnRemaining()) < 30) {
            this.fire(MAX_BULLET_POWER);
        }

    }

    public void onHitRobot(HitRobotEvent evnt) {
        if (this.teammates.containsKey(evnt.getName())) {
            double bearing = evnt.getBearing();
            turnRight(-bearing);
        } else {
            this.turnRight(evnt.getBearing());
            this.fire(MAX_BULLET_POWER);
            this.ahead(40);
        }
    }

    public void onHitWall(HitWallEvent e) {
        double bearing = e.getBearing();
        turnRight(-bearing);
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

    /**
     * onHitByBullet:  Turn perpendicular to the bullet, and move a bit.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));

        ahead(dist);
        dist *= -1;
        scan();
    }

    public void goAroundBattlefield() {
        // Turn to a wall
        this.turnLeft(this.getHeading() % 90);
        // Battlefield's width
        this.ahead(800);
        this.peek = true;
        this.turnLeft(90);
        while (true) {
            if(this.getTime()%5==0){
                this.direction*=-1;
            }
            this.ahead(800);
            this.turnLeft(90*this.direction);
        }
    }
}
