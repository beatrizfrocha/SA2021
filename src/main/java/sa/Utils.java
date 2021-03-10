package sa;

public class Utils {
    public static double euclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    public static double angleBetween(double xi, double yi, double xf, double yf){

        double hipotenusa = euclideanDistance(xi, yi, xf, yf);
        double adjacente = euclideanDistance(xi, yi, xi, yf);

        // Calcula-se o ângulo usando o teorema de pitágoras
        double cosseno = adjacente/hipotenusa;
        double angle = Math.acos(cosseno);

        // Transforma-se para graus
        return (180/ Math.PI)*angle;
    }

    public static double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
