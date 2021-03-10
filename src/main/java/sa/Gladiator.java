package sa;

import robocode.Droid;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.TeamRobot;

import java.io.IOException;

public class Gladiator extends TeamRobot implements Droid {

    private Rival rival;
    private boolean shooting = false;
    private boolean boss_dead = false;
    private boolean saviour_dead = false;
    private boolean battlefield_mode = false;

    public void run(){
    }

    public void onMessageReceived(MessageEvent evnt){
        Message msg = (Message) evnt.getMessage();
        switch (msg.getType()){
            case Message.REQUEST:
                this.informPosition();
                break;
            case Message.SHOOT:
                this.rival = msg.getRival();
                this.shoot_rival(rival);
                break;
        }
    }

    public void onRobotDeath(RobotDeathEvent evnt){
        String name = evnt.getName();
        if(name.equals(this.rival.getName())){
            this.rival.reconfigure();
            this.shooting = false;
        }
        if (name.equals("sa.Boss*")){
            this.boss_dead = true;
            if (this.saviour_dead){
                this.battlefield_mode = true;
                this.goAroundBattlefield();
            }
        }
        if (name.equals("sa.Saviour*")){
            this.saviour_dead = true;
            if(this.boss_dead){
                this.battlefield_mode = true;
                this.goAroundBattlefield();
            }
        }
    }

    public void informPosition(){
        Message msg = new Message(this.getName(), Message.INFO, this.getX(), this.getY());
        try{
            this.broadcastMessage(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
