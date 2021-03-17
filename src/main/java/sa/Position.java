package sa;

public class Position implements java.io.Serializable {
    private double x;
    private double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof Position)) return false;

        Position p = (Position) obj;

        return (int)p.getX() == (int)x && (int)p.getY() == (int)y;
    }

    public String toString() {
        return "(" + (int) x + ", " + (int) y + ")";
    }
}