import java.io.IOException;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static double[][] distances;
    static int TIME_LIMIT = 900;
    static long startTime;
    static boolean DEBUG = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 0)
            DEBUG = Boolean.parseBoolean(args[0]);
        startTime = System.currentTimeMillis();
        // Setup
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        double[][] coordinates = readCoordinates(pointsCount);
        distances = calculateDistances(pointsCount, coordinates);

        // Greedy
        short[] greedyTour = greedyTour(pointsCount);
        if (DEBUG) {
            for (short i : greedyTour)
                System.out.println(i);
            System.out.println("Greedy distance: " + tourDistance(greedyTour));
        }

        // 2-opt
        short[] twoOptTour = twoOpt(greedyTour);
        for (short i : twoOptTour)
            System.out.println(i);
        if (DEBUG) {
            System.err.println("2-opt distance: " + tourDistance(twoOptTour));
        }
    }

    static short[] twoOpt(short[] tour) {
        boolean foundBetterTour = twoOptPass(tour);
        while (foundBetterTour) {
            if (timeLimitPassed())
                return tour;
            foundBetterTour = twoOptPass(tour);
        }
        return tour;
    }

    static boolean twoOptPass(short[] tour) {
        int n = tour.length;
        for (int i = 0; i < tour.length - 1; i++) {
            for (int k = i + 1; k < tour.length; k++) {
                if (timeLimitPassed())
                    return false;

                if (i == 0 && (k + 1) == n)
                    continue;
                int jMinus;
                int j = tour[i];
                if (i == 0)
                    jMinus = tour[n - 1];
                else
                    jMinus = tour[i - 1];
                short l = tour[k];
                short lPlus = tour[(k + 1) % n];

                double oldDist = distance(jMinus, j) + distance(l, lPlus);
                double newDist = distance(jMinus, l) + distance(j, lPlus);
                if (newDist < oldDist) {
                    twoOptSwap(tour, i, k);
                    return true;
                }
            }
        }
        return false;
    }

    static void twoOptSwap(short[] tour, int x, int y) {
        short tmp;
        while (y > x) {
            tmp = tour[y];
            tour[y] = tour[x];
            tour[x] = tmp;
            x++;
            y--;
        }
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
            for (int j = 0; j < i; j++) {
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

    static boolean timeLimitPassed() {
        return System.currentTimeMillis() - startTime > TIME_LIMIT;
    }
}
