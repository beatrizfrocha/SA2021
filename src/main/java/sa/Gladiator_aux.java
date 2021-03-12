package sa;

import java.io.IOException;
import robocode.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;
import static sa.Utils.angleBetween;
import static sa.Utils.euclideanDistance;

public class Gladiator_aux extends TeamRobot {


    public void run() {

        for (int i = 0; i < 60; i++) {
            this.doNothing();
        }
        Message msg = new Message(Message.MOVE,18,18);
        try {
            broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
