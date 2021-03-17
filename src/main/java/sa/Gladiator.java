package sa;

import robocode.*;

import java.io.IOException;

import static robocode.Rules.MAX_BULLET_POWER;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.normalizeBearing;

public class Gladiator extends TeamRobot implements Droid {

    private Rival rival;
    private boolean shooting = false;
    private boolean boss_dead = false;
    private boolean saviour_dead = false;
    private boolean battlefield_mode = false;
    private boolean peek = false;
    private int direction = 1;

    public void run() {
        this.informPosition();
        this.setAdjustGunForRobotTurn(true);
    }

    public void onMessageReceived(MessageEvent evnt) {
        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()) {
            case Message.REQUEST:
                this.informPosition();
                break;
            case Message.SHOOT:
                this.rival = msg.getRival();
                this.shoot_rival(this.rival);
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
        if (this.isTeammate(evnt.getName())) {
            this.ahead(100);
        } else {
            this.turnRight(evnt.getBearing());
            this.fire(MAX_BULLET_POWER);
            this.ahead(40);
        }
    }

    public void onRobotDeath(RobotDeathEvent evnt) {
        String name = evnt.getName();
        if (name.equals(this.rival.getName())) {
            this.rival.reconfigure();
            this.shooting = false;
        }
        if (name.equals("sa.Boss*")) {
            this.boss_dead = true;
            if (this.saviour_dead) {
                this.battlefield_mode = true;
                this.goAroundBattlefield();
            }
        }
        if (name.equals("sa.Saviour*")) {
            this.saviour_dead = true;
            if (this.boss_dead) {
                this.battlefield_mode = true;
                this.goAroundBattlefield();
            }
        }
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

    public void informPosition() {
        Message msg = new Message(this.getName(), Message.INFO, this.getX(), this.getY());
        try {
            this.broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
