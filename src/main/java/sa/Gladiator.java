package sa;

import robocode.*;

import java.io.IOException;

public class Gladiator extends TeamRobot implements Droid {

    private Rival rival;
    private boolean shooting = false;
    private boolean boss_dead = false;
    private boolean saviour_dead = false;
    private boolean battlefield_mode = false;

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
                this.shoot_rival(rival);
                break;
        }
    }

    private void shoot_rival(Rival rival) {

    }

    public void onHitRobot(HitRobotEvent evnt) {
        if (this.isTeammate(evnt.getName())) {
            this.ahead(100);
        } else {
            this.turnRight(evnt.getBearing());
            this.fire(3);
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
        this.turnLeft(90);
        while (true) {
            this.ahead(800);
            this.turnLeft(90);
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
