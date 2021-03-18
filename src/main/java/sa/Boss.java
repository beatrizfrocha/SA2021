package sa;

import robocode.*;

import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static sa.Utils.*;

public class Boss extends TeamRobot {

    private int hits = 0;
    private Position destino;

    // Quadrantes
    private Map<Integer, SimpleEntry<Position,Position>> quadrantes;

    // Amigos
    private Map<String, Position> teammates = new HashMap<>();
    private boolean sicko_mode = false; // modo de ataque
    private int gladiators_alive = 3;

    // Inimigos
    private Map<String, Rival> enemies = new HashMap<>();
    private int enemies_alive = 5;

    // ----------------------- Main ------------------------

    public void run() {
        this.quadrantes = initQuadrantes(getBattleFieldWidth(),getBattleFieldHeight());

        this.teammates = getNewTeammates(this);
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        turnRadarLeft(360);

        while(true){
            Position p = getBetterPosition();
            System.out.println("Moving to " + p.toString());
            this.destino = p;
            move(p,this);
            System.out.println(".............................");
        }
    }

    // ---------------------- Events ----------------------

    public void onMessageReceived(MessageEvent event) {
        Message message = (Message) event.getMessage();

        if(message.getType() == Message.INFO){
            //msgsReceived++;
            teammates.put(message.getSender(), message.getPosition());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        if(sicko_mode){
            // modo de ataque
        }
        else{
            // Encontra inimigo
            if (!this.teammates.containsKey(e.getName())) {
                Position p = this.findPosition(e);
                Rival enemy = new Rival(e,p);
                enemies.put(e.getName(), enemy);
            }
            // Encontra amigo
            else{

            }
        }

    }

    public void onRobotDeath(RobotDeathEvent evnt) {
        String name = evnt.getName();

        // Morreu inimigo
        if(!this.teammates.containsKey(name)){
            enemies_alive--;
            enemies.remove(name);
        }
        // Morreu amigo
        else {
            if (name.contains("sa.Gladiator*")) {
                gladiators_alive--;
                if (gladiators_alive == 0)
                    sicko_mode = true;
            }
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        this.back(50);
        if(this.hits<5){
            this.turnLeft(45);
            this.hits++;
        }
        else{
            this.turnRight(45);
        }
        this.ahead(60);
        move(destino,this);
    }

    // ---------------------- Comunication ----------------------

    /*public void attack(Position p){
        //Message msg = new Message(Message.ATTACK,p);
        try {
            this.broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // ---------------------- Util ----------------------

    public Rival selectTarget() {
        double totalDistance = 0;
        double minTotalDistance = 10000;
        Rival res = null;
        teammates.put(getName(), new Position(getX(), getY()));
        for (Rival enemy : enemies.values()) {
            for (Position teammate : teammates.values()) {
                double distance = euclideanDistance(enemy.getX(), enemy.getY(), teammate.getX(), teammate.getY());
                totalDistance += distance;
            }
            if (totalDistance < minTotalDistance) {
                minTotalDistance = totalDistance;
                res = enemy;
            }
        }

        return res;
    }

    private Position findPosition(ScannedRobotEvent e){
        double angleToEnemy = e.getBearing();

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((this.getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = this.getX() + Math.sin(angle) * e.getDistance();
        double enemyY = this.getY() + Math.cos(angle) * e.getDistance();

        return new Position(enemyX,enemyY);
    }

    private Position getBetterPosition(){

        int num_quadr1 = countEnemiesInQuadrant(1);
        int num_quadr2 = countEnemiesInQuadrant(2);
        int num_quadr3 = countEnemiesInQuadrant(3);
        int num_quadr4 = countEnemiesInQuadrant(4);

        System.out.println("1 = " + num_quadr1 + " inimigos");
        System.out.println("2 = " + num_quadr2 + " inimigos");
        System.out.println("3 = " + num_quadr3 + " inimigos");
        System.out.println("4 = " + num_quadr4 + " inimigos");

        int best_quadrant;

        if(num_quadr1 <= Math.min(num_quadr2,Math.min(num_quadr3,num_quadr4)))
            best_quadrant = 1;
        else if(num_quadr2 <= Math.min(num_quadr3,num_quadr4))
            best_quadrant = 2;
        else if(num_quadr3 <= num_quadr4)
            best_quadrant = 3;
        else
            best_quadrant = 4;

        double x_min = quadrantes.get(best_quadrant).getKey().getX();
        double y_min = quadrantes.get(best_quadrant).getKey().getY();
        double x_max = quadrantes.get(best_quadrant).getValue().getX();
        double y_max = quadrantes.get(best_quadrant).getValue().getY();

        double rand_x = Math.random() * (x_max - x_min) + x_min;
        double rand_y = Math.random() * (y_max - y_min) + y_min;

        return new Position(rand_x,rand_y);
    }

    private int countEnemiesInQuadrant(int quadrant){

        double x_min = quadrantes.get(quadrant).getKey().getX();
        double y_min = quadrantes.get(quadrant).getKey().getY();
        double x_max = quadrantes.get(quadrant).getValue().getX();
        double y_max = quadrantes.get(quadrant).getValue().getY();

        int count = 0;
        for(Rival r: this.enemies.values()){
            if(r.getX() < x_max && r.getY() < y_max && r.getX() > x_min && r.getY() > y_min)
                count++;
        }
        return count;
    }
}
