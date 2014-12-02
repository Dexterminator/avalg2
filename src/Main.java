import java.io.IOException;
import java.util.Arrays;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static double[][] distances;
    static int TIME_LIMIT = 1100;
    static long startTime;

    public static void main(String[] args) throws IOException {
        startTime = System.currentTimeMillis();
        // Setup
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        double[][] coordinates = readCoordinates(pointsCount);
        distances = calculateDistances(pointsCount, coordinates);

        // Greedy
        short[] greedyTour = greedyTour(pointsCount);
//        for (short i : greedyTour)
//            io.println(i);
//        io.println("Greedy distance: " + tourDistance(greedyTour));

        // Testing
//        short[] testSwap = twoOptSwap(new short[]{0, 1, 2, 3, 4, 5}, 1, 3);
//        System.out.println(Arrays.toString(testSwap));

        // 2-opt
        short[] twoOptTour = twoOpt(greedyTour);
        for (short i : twoOptTour)
            System.out.println(i);
//        System.err.println("2-opt distance: " + tourDistance(twoOptTour));
    }

    static short[] twoOpt(short[] tour) {
        short[] bestTour = tour;
        short[] passTour = twoOptPass(bestTour);
        while (passTour != null) {
            if (timeLimitPassed())
                return bestTour;
            bestTour = passTour;
            passTour = twoOptPass(bestTour);
        }
        return bestTour;
    }

    static boolean timeLimitPassed() {
        return System.currentTimeMillis() - startTime > TIME_LIMIT;
    }

    static short[] twoOptPass(short[] tour) {
        double bestDistance = tourDistance(tour);
        double newDistance;
        for (int i = 0; i < tour.length - 1; i++) {
            for (int k = i + 1; k < tour.length; k++) {
                if (timeLimitPassed())
                    return null;
                short[] newTour = twoOptSwap(tour, i, k);
                newDistance = tourDistance(newTour);
                if (newDistance < bestDistance)
                    return newTour;
            }
        }
        return null;
    }

    static short[] twoOptSwap(short[] tour, int swapStart, int swapEnd) {
        short[] newTour = new short[tour.length];
        for (int i = 0; i < swapStart; i++) {
            newTour[i] = tour[i];
        }
        for (int i = swapStart; i <= swapEnd; i++) {
            newTour[i] = tour[swapStart+swapEnd-i];
        }
        for (int i = swapEnd + 1; i < tour.length; i++) {
            newTour[i] = tour[i];
        }
        return newTour;
    }

    static short[] greedyTour(int length) {
        short[] tour = new short[length];
        boolean[] used = new boolean[tour.length];
        tour[0] = 0;
        used[0] = true;
        for (int i = 1; i < tour.length; i++) {
            short best = -1;
            double bestDist = Double.MAX_VALUE;
            for (short j = 0; j < tour.length; j++) {
                double currDist = distance(tour[i-1], j);
                if (!used[j] && (best == -1 || currDist < bestDist)) {
                    best = j;
                    bestDist = currDist;
                }
            }
            tour[i] = best;
            used[best] = true;
        }
        return tour;
    }

    private static double tourDistance(short[] tour) {
        double length = 0.0;
        for (int i = 1; i < tour.length; i++) {
            length += distance(tour[i-1], tour[i]);
        }
        length += distance(tour[0], tour[tour.length-1]);
        return length;
    }

    private static double distance(int i, int j) {
        return distances[i][j];
    }

    private static double[][] calculateDistances(int pointsCount, double[][] coordinates) {
        double [][] distances = new double[pointsCount][pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            for (int j = 0; j <= i; j++) {
                distances[i][j] = coordinateDistance(coordinates[i], coordinates[j]);
                distances[j][i] = distances[i][j];
            }
        }
        return distances;
    }

    static double coordinateDistance(double[] coordinate1, double[] coordinate2) {
        double paren1 = Math.pow(coordinate1[0] - coordinate2[0], 2);
        double paren2 = Math.pow(coordinate1[1] - coordinate2[1], 2);
        double distance = Math.sqrt(paren1 + paren2);
        return distance;
    }

    private static double[][] readCoordinates(int pointsCount) {
        double[][] coordinates = new double[pointsCount][2];
        for (int i = 0; i < pointsCount; i++) {
            coordinates[i][0] = io.getDouble();
            coordinates[i][1] = io.getDouble();
        }
        return coordinates;
    }
}
