import java.io.IOException;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static short[] tour;
    static double[][] coordinates;

    public static void main(String[] args) throws IOException {
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        tour = new short[pointsCount];
        coordinates = new double[pointsCount][2];
        for (int i = 0; i < pointsCount; i++) {
            double coord1 = io.getDouble();
            double coord2 = io.getDouble();
            coordinates[i][0] = coord1;
            coordinates[i][1] = coord2;
        }
        double distance = distance(coordinates[0], coordinates[1]);
        System.out.println(distance);
    }

    static double distance(double[] coordinate1, double[] coordinate2) {
        double paren1 = Math.pow(coordinate1[0] - coordinate2[0], 2);
        double paren2 = Math.pow(coordinate1[1] - coordinate2[1], 2);
        double distance = Math.sqrt(paren1 + paren2);
        return distance;
    }

    static void greedyTour() {

    }
}
