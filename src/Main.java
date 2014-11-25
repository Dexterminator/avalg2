import java.io.IOException;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;

    public static void main(String[] args) throws IOException {
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        short[] tour = new short[pointsCount];
        double[][] coordinates = new double[pointsCount][2];
        for (int i = 0; i < pointsCount; i++) {
            coordinates[i][0] = io.getDouble();
            coordinates[i][1] = io.getDouble();
        }
        short[] greedyTour = greedyTour(tour, coordinates);
        for (short i : greedyTour) {
            System.out.println(i);
        }
    }

    static double distance(double[] coordinate1, double[] coordinate2) {
        double paren1 = Math.pow(coordinate1[0] - coordinate2[0], 2);
        double paren2 = Math.pow(coordinate1[1] - coordinate2[1], 2);
        double distance = Math.sqrt(paren1 + paren2);
        return distance;
    }


    static short[] greedyTour(short[] tour, double[][]coordinates) {
        boolean[] used = new boolean[tour.length];
        tour[0] = 0;
        used[0] = true;
        for (int i = 1; i < tour.length; i++) {
            short best = -1;
            double[] prevCoord = coordinates[tour[i-1]];
            for (short j = 0; j < tour.length; j++) {
                double[] currCoord = coordinates[j];
                if (!used[j] && (best == -1 || distance(prevCoord, currCoord) <
                        distance(prevCoord, coordinates[best]))) {
                    best = j;
                }
            }
            tour[i] = best;
            used[best] = true;
        }
        return tour;
    }
}
