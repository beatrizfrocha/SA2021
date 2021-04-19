package sa.fase3;

import robocode.*;
import sa.Position;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.*;

public class Boss extends TeamRobot {

    private int dist = 50;

    // Friends
    public Map<String, Position> teammates = new HashMap<>();
    public boolean sicko_mode = false; // modo de ataque
    public int gladiators_alive = 2;

    // Enemies
    private Map<String, Rival> enemies = new HashMap<>();
    private Rival current_rival;
    private int enemiesToScan = 5;
    private int scannedEnemies = 0;
    private int aliveEnemies = 5;

    // ----------------------- Main ------------------------

    public void run() {

        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }
        System.out.println("My team is " + this.teammates.toString());

        this.setBodyColor(new Color(200, 200, 0));
        this.setGunColor(new Color(200, 200, 0));
        this.setRadarColor(new Color(200, 200, 0));
        this.setAdjustGunForRobotTurn(true);

        this.comeWithMe(new Position(this.getX(),this.getY()));

        while (true) {
            this.turnGunRight(10); // Scans automatically
        }
    }

    // ---------------------- Events ----------------------

    public void onMessageReceived(MessageEvent event) {
        Message message = (Message) event.getMessage();

        if(message.getType() == Message.INFO){
            this.teammates.put(message.getSender(), message.getPosition());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        // Attacks enemy
        if(this.sicko_mode && !this.teammates.containsKey(e.getName())){

            // ---------------------------------------- Shoot ----------------------------------------
            double absoluteBearing = this.getHeading() + e.getBearing();
            double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - this.getGunHeading());

            if (Math.abs(bearingFromGun) <= 3) {
                this.turnGunRight(bearingFromGun);
                if (this.getGunHeat() == 0) {
                    this.fire(Math.min(3 - Math.abs(bearingFromGun), this.getEnergy() - .1));
                }
            } else {
                this.turnGunRight(bearingFromGun);
            }
            if (bearingFromGun == 0) {
                this.scan();
            }
        }
        else{
            Position p = findPosition(this,e);
            // Finds enemy
            if (!this.teammates.containsKey(e.getName())) {

                this.scannedEnemies++;
                Rival enemy = new Rival(e,p);
                this.enemies.put(e.getName(), enemy);

                if(this.scannedEnemies == this.enemiesToScan){
                    this.current_rival = this.selectTarget();
                    this.attackForMe(this.current_rival);
                }

                // Updates enemy's current position
                if (this.current_rival != null && e.getName().equals(this.current_rival.getName())) {
                    this.current_rival.update(e,p);
                    this.attackForMe(this.current_rival);
                }
            }
        }

    }

    public void onHitByBullet(HitByBulletEvent e) {

        if(this.enemies.containsKey(e.getName())) {
            Rival r = new Rival(e);
            this.avengeMe(r);
        }
        this.turnRight(normalRelativeAngleDegrees(90 - (this.getHeading() - e.getHeading())));
        this.ahead(this.dist);
        this.dist *= -1;
        this.scan();

        this.comeWithMe(new Position(this.getX(),this.getY()));
    }

    public void onRobotDeath(RobotDeathEvent evnt) {
        String name = evnt.getName();

        // Enemy is dead
        if(!this.teammates.containsKey(name)){
            this.enemies.remove(name);
            this.enemiesToScan--;

            // When the current enemy dies
            if (this.current_rival != null && evnt.getName().equals(this.current_rival.getName())) {
                this.current_rival.reconfigure();
                this.requestInfoFromDroids();
                this.current_rival = this.selectTarget();
                this.enemiesToScan = --this.aliveEnemies;
                this.scannedEnemies = 0;
            }
        }
        // Friend is dead
        else {
            this.teammates.remove(evnt.getName());
            if (name.contains("Gladiator")) {
                this.gladiators_alive--;
                if (this.gladiators_alive == 0)
                    this.sicko_mode = true;
            }
        }
    }

    // ---------------------- Comunication ----------------------

    public void attackForMe(Rival r){
        Message msg = new Message(Message.ATTACK,r);
        try {
            for(Map.Entry<String,Position> mates: this.teammates.entrySet())
                if (mates.getKey().contains("Gladiator"))
                    this.sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void avengeMe(Rival r){
        Message msg = new Message(Message.ATTACK,r);
        try {
            for(Map.Entry<String,Position> mates: this.teammates.entrySet())
                if (mates.getKey().contains("Avenger"))
                    this.sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestInfoFromDroids() {
        Message msg = new Message(Message.REQUEST);
        try {
            for(Map.Entry<String,Position> mates: this.teammates.entrySet())
                if (mates.getKey().contains("Gladiator"))
                    this.sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void comeWithMe(Position p){
        Message msg = new Message(Message.COME_WITH_ME,p);
        try {
            for(Map.Entry<String,Position> mates: this.teammates.entrySet())
                if (mates.getKey().contains("Saviour"))
                    this.sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void informTeammates(){
        Message msg = new Message(Message.INFO);

        try{
            this.broadcastMessage(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // ---------------------- Util ----------------------

    public Rival selectTarget() {
        double totalDistance = 0;
        double minTotalDistance = 10000;
        Rival res = null;
        this.teammates.put(getName(), new Position(getX(), getY()));
        for (Rival enemy : this.enemies.values()) {
            for (Map.Entry<String,Position> pair: this.teammates.entrySet()){
                if(pair.getKey().contains("Gladiator")) {
                    double distance = euclideanDistance(enemy.getX(), enemy.getY(), pair.getValue().getX(), pair.getValue().getY());
                    totalDistance += distance;
                }
            }
            if (totalDistance < minTotalDistance) {
                minTotalDistance = totalDistance;
                res = enemy;
            }
        }

        if(res != null)
            System.out.println("Our current rival is: " + res.getName());
        else
            System.out.println("Rival is null.");
        return res;
    }
}
