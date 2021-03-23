package sa;

public class Position implements java.io.Serializable {
    private double x;
    private double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof Position)) return false;

        Position p = (Position) obj;

        return (int)p.getX() == (int)this.x && (int)p.getY() == (int)this.y;
    }

    public String toString() {
        return "(" + (int) this.x + ", " + (int) this.y + ")";
    }
}