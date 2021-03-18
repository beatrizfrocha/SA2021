package sa.fase3;

import sa.Position;

import java.io.Serializable;

public class Message implements Serializable {

    private String sender;
    private String receiver;
    private String content;
    private Rival rival;
    private int type;
    private Position p;

    public final static int INFO = 0;
    public final static int ATTACK = 1;
    public final static int REQUEST = 2;
    public final static int HELP = 3;
    public final static int COME_WITH_ME = 4;

    public Message() {
        this.sender = null;
        this.receiver = null;
        this.content = null;
        this.rival = null;
        this.type = 0;
        this.p = null;
    }

    public Message(String sender, int type, Position p) {
        this.sender = sender;
        this.type = type;
        this.p = p;
    }

    public Message(int type, Rival r) {
        this.type = type;
        this.rival = r;
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
        return p.getX();
    }

    public double getY() {
        return p.getY();
    }

    public Position getPosition() {
        return p;
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
        p.setX(x);
    }

    public void setY(double y) {
        p.setY(y);
    }

}
