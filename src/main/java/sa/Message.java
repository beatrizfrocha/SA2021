package sa;

import java.io.Serializable;

public class Message implements Serializable {

    private String sender;
    private String receiver;
    private String content;
    private Rival rival;
    private int type;
    private double x;
    private double y;

    public final static int INFO = 0;
    public final static int SHOOT = 1;
    public final static int REQUEST = 2;
    public final static int MOVE = 3;
    public final static int TURN = 4;

    public Message() {
        this.sender = null;
        this.receiver = null;
        this.content = null;
        this.rival = null;
        this.type = 0;
        this.x = 0;
        this.y = 0;
    }

    public Message(String sender, int type, double x, double y) {
        this.sender = sender;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public Message(int type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public Message(int type) {
        this.type = type;
    }

    public String getSender() {
        return this.sender;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public String getContent() {
        return this.content;
    }

    public Rival getRival() {
        return this.rival;
    }

    public int getType() {
        return this.type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

}
