package sa.fase3;

import robocode.*;
import sa.Position;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.*;

public class Boss extends TeamRobot {

    private int dist = 50;

    // Quadrantes
    private Map<Integer, SimpleEntry<Position,Position>> quadrantes;

    // Amigos
    public Map<String, Position> teammates = new HashMap<>();
    private boolean sicko_mode = false; // modo de ataque
    private int gladiators_alive = 2;

    // Inimigos
    private Map<String, Rival> enemies = new HashMap<>();
    private Rival current_rival;
    private Rival avengerEnemy = null;
    private int enemiesToScan = 5;
    private int scannedEnemies = 0;
    private int aliveEnemies = 5;

    // ----------------------- Main ------------------------

    public void run() {

        informPosition(this);
        for (int i = 0; i < 10; i++) {
            this.doNothing();
        }
        System.out.println("My team is " + teammates.toString());

        setBodyColor(new Color(200, 200, 0));
        setGunColor(new Color(200, 200, 0));
        setRadarColor(new Color(200, 200, 0));
        this.setAdjustGunForRobotTurn(true);

        while (true) {
            turnGunRight(10); // Scans automatically
        }
    }

    // ---------------------- Events ----------------------

    public void onMessageReceived(MessageEvent event) {
        Message message = (Message) event.getMessage();

        if(message.getType() == Message.INFO){
            teammates.put(message.getSender(), message.getPosition());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        // Ataca inimigo
        if(sicko_mode && !this.teammates.containsKey(e.getName())){

            // ---------------------------------------- Shoot ----------------------------------------
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
        else{
            Position p = findPosition(this,e);
            // Encontra inimigo
            if (!this.teammates.containsKey(e.getName())) {

                scannedEnemies++;
                Rival enemy = new Rival(e,p);
                enemies.put(e.getName(), enemy);

                if(scannedEnemies == enemiesToScan){
                    this.current_rival = selectTarget();
                    attackForMe(current_rival);
                }

                // Atualiza posição do rival atual
                if (current_rival != null && e.getName().equals(current_rival.getName())) {
                    current_rival.update(e,p);
                    attackForMe(current_rival);
                }
                else if(current_rival == null){
                    System.out.println("current rival = null");
                }

                // Remove inimigo do avenger
                if (avengerEnemy != null && e.getName().equals(avengerEnemy.getName())) {
                    avengerEnemy = null;
                }
            }
        }

    }

    public void onHitByBullet(HitByBulletEvent e) {

        if(this.avengerEnemy == null && !this.teammates.containsKey(e.getName())) {
            Rival r = new Rival(e);
            avengeMe(r);
            this.avengerEnemy = r;
        }

        /*turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));

        ahead(dist);
        dist *= -1;
        scan();*/
        //comeWithMe(new Position(this.getX(),this.getY()));
    }

    public void onRobotDeath(RobotDeathEvent evnt) {
        String name = evnt.getName();

        // Morreu inimigo
        if(!this.teammates.containsKey(name)){
            enemies.remove(name);
            enemiesToScan--;

            // Quando o rival atual morre
            if (current_rival != null && evnt.getName().equals(current_rival.getName())) {
                current_rival.reconfigure();
                requestInfoFromDroids();
                this.current_rival = selectTarget();
                enemiesToScan = --aliveEnemies;
                scannedEnemies = 0;

            }
        }
        // Morreu amigo
        else {
            this.teammates.remove(evnt.getName());
            if (name.contains("Gladiator")) {
                gladiators_alive--;
                if (gladiators_alive == 0)
                    sicko_mode = true;
            }
        }
    }

    // ---------------------- Comunication ----------------------

    public void attackForMe(Rival r){
        Message msg = new Message(Message.ATTACK,r);
        try {
            for(Map.Entry<String,Position> mates: teammates.entrySet())
                if (mates.getKey().contains("Gladiator"))
                    sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void avengeMe(Rival r){
        Message msg = new Message(Message.ATTACK,r);
        try {
            for(Map.Entry<String,Position> mates: teammates.entrySet())
                if (mates.getKey().contains("Avenger"))
                    sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestInfoFromDroids() {
        Message msg = new Message(Message.REQUEST);
        try {
            for(Map.Entry<String,Position> mates: teammates.entrySet())
                if (mates.getKey().contains("Gladiator"))
                    sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void comeWithMe(Position p){
        Message msg = new Message(Message.COME_WITH_ME,p);
        try {
            for(Map.Entry<String,Position> mates: teammates.entrySet())
                if (mates.getKey().contains("Saviour"))
                    sendMessage(mates.getKey(), msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void informTeammates(){
        Message msg = new Message(Message.INFO);

        try{
            broadcastMessage(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // ---------------------- Util ----------------------

    public Rival selectTarget() {
        double totalDistance = 0;
        double minTotalDistance = 10000;
        Rival res = null;
        teammates.put(getName(), new Position(getX(), getY()));
        for (Rival enemy : enemies.values()) {
            for (Map.Entry<String,Position> pair: teammates.entrySet()){
                if(pair.getKey().contains("Gladiator")) {
                    System.out.println("entrei num glad");
                    double distance = euclideanDistance(enemy.getX(), enemy.getY(), pair.getValue().getX(), pair.getValue().getY());
                    totalDistance += distance;
                }
            }
            System.out.println("totalDistance = " + totalDistance);
            System.out.println("minTotalDistance = " + minTotalDistance);
            if (totalDistance < minTotalDistance) {
                minTotalDistance = totalDistance;
                res = enemy;
            }
        }

        if(res != null)
            System.out.println("Our current rival is: " + res.toString());
        else
            System.out.println("Rival is null.");
        return res;
    }
}
