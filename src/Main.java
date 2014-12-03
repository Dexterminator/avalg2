import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static int[][] distances;
    static int TIME_LIMIT = 1500;
    static long startTime;
    static boolean DEBUG = false;
    private static Random random;
    static int numNeighbors = 20;

    private static class Neighbor implements Comparable<Neighbor>{
        public int index;
        public double distance;

        public Neighbor(int index, double distance){
            this.index = index;
            this.distance = distance;
        }
        @Override
        public int compareTo(Neighbor n){
            if(this.distance < n.distance)
                return -1;
            else if(this.distance > n.distance)
                return 1;
            else
                return 0;
        }

        @Override
        public String toString(){
            return this.index+"="+distance;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0)
            DEBUG = Boolean.parseBoolean(args[0]);
        startTime = System.currentTimeMillis();
        random = new Random();
        // Setup
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        Point2D.Double[] coordinates = readCoordinates(pointsCount);
        calculateDistances(pointsCount, coordinates);

        // Greedy
        long greedyTime = System.currentTimeMillis();
        short[] greedyTour = greedyTour(pointsCount);
        int greedyTourDistance = -1;
        if (DEBUG) {
            System.out.println("Greedy time: " + (System.currentTimeMillis() - greedyTime));
            greedyTourDistance = tourDistance(greedyTour);
        }

        // 2-opt
        short[] twoOptTour = twoOpt(greedyTour);
//        short[] shuffledTour = shuffledTour(twoOptTour);
//        twoOpt(shuffledTour);
//        short[] twoOptTour = twoOpt(greedyTour);
        for (short i : twoOptTour)
            System.out.println(i);
        if (DEBUG) {
            System.out.println("Greedy distance: " + greedyTourDistance);
            System.err.println("2-opt distance: " + tourDistance(twoOptTour));
            System.out.println("Time taken: "  + (System.currentTimeMillis() - startTime));
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
                if(j == l)
                    continue;

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
    /*
    static boolean twoOptOnlyNearest(short[] tour){
        int n = tour.length;
        for (int i = 0; i < tour.length - 1; i++) {
            for (int k = i + 1; k < neighborList.get((int) tour[i]).length ; k++) {
                if (timeLimitPassed())
                    return false;

                if (i == 0 && (k + 1) == n)
                    continue;
                int jMinus;
                if (i == 0)
                    jMinus = tour[n - 1];
                else
                    jMinus = tour[i - 1];
                //int j = tour[i];
                Neighbor jNeighbor = neighborList.get((int) tour[jMinus])[i];
                int j = jNeighbor.index;
                Neighbor lNeighbor = neighborList.get((int) tour[i])[k];
                Neighbor lPlusNeighbor = neighborList.get((int) tour[i])[(k + 1) % n];
                int l = lNeighbor.index;
                int lPlus = lPlusNeighbor.index;

                double oldDist = distance(jMinus, j) + distance(l, lPlus);
                double newDist = distance(jMinus, l) + distance(j, lPlus);
                if (newDist < oldDist) {
                    twoOptSwap(tour, i, k);
                    //neighborList.get(jMinus)[j] = lNeighbor;
                    //neighborList.get(j)[lPlus] = lPlusNeighbor;

                    return true;
                }
            }
        }
        return false;
    }
    */

    static boolean twoOptPassTryAll(short[] tour) {
        int n = tour.length;
        int mini = 0;
        int mink = 0;
        boolean foundBetterPath = false;
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
                if(j == l)
                    continue;
                short lPlus = tour[(k + 1) % n];

                double oldDist = distance(jMinus, j) + distance(l, lPlus);
                double newDist = distance(jMinus, l) + distance(j, lPlus);
                if (newDist < oldDist) {
                    mini = i;
                    mink = k;
                    foundBetterPath = true;
                }
            }
        }
        if (foundBetterPath)
            twoOptSwap(tour, mini, mink);
        return foundBetterPath;
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

    static short[] shuffledTour(short[] tour) {
        short[] shuffledTour = Arrays.copyOf(tour, tour.length);
        for (int i = 0; i < 20; i++) {
            int index1 = random.nextInt(tour.length);
            int index2 = random.nextInt(tour.length);
            short tmp = tour[index1];
            shuffledTour[index1] = tour[index2];
            shuffledTour[index2] = tmp;
        }
        return shuffledTour;
    }

    static short[] newShuffledTour(short[] tour) {
        short[] shuffledTour = Arrays.copyOf(tour, tour.length);
        int index1 = random.nextInt(tour.length);
        int index2 = random.nextInt(tour.length);

        twoOptSwap(shuffledTour, index1, index2);
        return shuffledTour;
    }


    static short[] randomStartGreedyTour(int length) {
        short[] tour = new short[length];
        boolean[] used = new boolean[tour.length];

        short first = (short) random.nextInt(length);
        tour[0] = first;
        used[first] = true;
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

//    static short[] ultimateGreedy(int length) {
//        short[] tour = new short[length];
//        boolean[] used = new boolean[tour.length];
//        short first = (short) random.nextInt(length);
//        tour[0] = first;
//        used[first] = true;
//        for (int i = 1; i < tour.length; i++) {
//            for (Neighbor neighbor : neighborLists[tour[i]]) {
//                short j = (short) neighbor.index;
//                if (!used[j]) {
//                    tour[i] = j;
//                    used[j] = true;
//                    break;
//                }
//            }
//        }
//        return tour;
//    }

    private static int tourDistance(short[] tour) {
        int length = 0;
        for (int i = 1; i < tour.length; i++) {
            length += distance(tour[i-1], tour[i]);
        }
        length += distance(tour[0], tour[tour.length-1]);
        return length;
    }

    private static double distance(int i, int j) {
        return distances[i][j];
    }

    private static void calculateDistances(int pointsCount, Point2D.Double[] coordinates) {
        distances = new int[pointsCount][pointsCount];
        for(int i = 0; i < pointsCount; i++) {
            for(int j = 0; j <= i; j++) {
                int dist = (int) Math.round(coordinates[i].distance(coordinates[j]));
                distances[i][j] = dist;
                distances[j][i] = dist;
            }
        }
    }

    private static Point2D.Double[] readCoordinates(int pointsCount) {
        Point2D.Double[] coordinates = new Point2D.Double[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            Point2D.Double point = new Point2D.Double(io.getDouble(), io.getDouble());
            coordinates[i] = point;
        }
        return coordinates;
    }

    static boolean timeLimitPassed() {
        return System.currentTimeMillis() - startTime > TIME_LIMIT;
    }
}
