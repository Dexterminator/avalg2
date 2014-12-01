import java.io.IOException;
import java.util.Arrays;

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
        short[] greedyTour = greedyTour(pointsCount, coordinates);
        /*
        for (short i : greedyTour) {
            io.println(i);
        }
        */
        //System.err.println("Distance: " + TwoOpt.tourDistance(greedyTour, coordinates));

        short[] twoOptTour = TwoOpt.twoOpt(greedyTour, coordinates);
        for (short i : twoOptTour) {
            io.println(i);
        }
        io.flush();

        //System.err.println("Distance: " + TwoOpt.tourDistance(twoOptTour, coordinates));
    }

    static short[] greedyTour(int length, double[][]coordinates) {
        short[] tour = new short[length];
        boolean[] used = new boolean[tour.length];
        tour[0] = 0;
        used[0] = true;
        for (int i = 1; i < tour.length; i++) {
            short best = -1;
            double[] prevCoord = coordinates[tour[i-1]];
            double bestDist = Double.MAX_VALUE;
            for (short j = 0; j < tour.length; j++) {
                double[] currCoord = coordinates[j];
                double currDist = TwoOpt.distance(prevCoord, currCoord);
                if (!used[j] && (best == -1 || currDist <
                        bestDist)) {
                    best = j;
                    bestDist = currDist;
                }
            }
            tour[i] = best;
            used[best] = true;
        }
        return tour;
    }
}
