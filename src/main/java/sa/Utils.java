package sa;

public class Utils {
    public static double distanciaEntrePontos(double xi, double yi, double xf, double yf){
        return Math.sqrt((Math.pow((xi - xf), 2)) + (Math.pow((yi - yf), 2)));
    }

    public static double angleBetween(double xi, double yi, double xf, double yf){

        double hipotenusa = distanciaEntrePontos(xi, yi, xf, yf);
        double adjacente = distanciaEntrePontos(xi, yi, xi, yf);

        // Calcula-se o ângulo usando o teorema de pitágoras
        double cosseno = adjacente/hipotenusa;
        double angle = Math.acos(cosseno);

        // Transforma-se para graus
        return (180/ Math.PI)*angle;
    }
}
