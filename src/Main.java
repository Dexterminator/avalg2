import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static double[][] distances;
    static int TIME_LIMIT = 1500;
    static long startTime;
    static boolean DEBUG = false;
    private static Random random;
    //static HashMap<Integer, Neighbor[]> neighborList;

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
        double[][] coordinates = readCoordinates(pointsCount);
        calculateDistances(pointsCount, coordinates);

        // Greedy
        short[] greedyTour = randomStartGreedyTour(pointsCount);
        double greedyTourDistance = -1;
        if (DEBUG) {
            greedyTourDistance = tourDistance(greedyTour);
        }

        // 2-opt
//        short[] twoOptTour = twoOpt(greedyTour);
//        short[] shuffledTour = shuffledTour(twoOptTour);
//        twoOpt(shuffledTour);
        /*
        if(DEBUG) {
            for(int i = 0; i < neighborList.size(); i++){
                System.out.println(i + ": " + Arrays.toString(neighborList.get(i)));
            }
        }*/
        short[] twoOptTour = twoOpt(greedyTour);
        for (short i : twoOptTour)
            System.out.println(i);
        if (DEBUG) {
            System.out.println("Greedy distance: " + greedyTourDistance);
            System.err.println("2-opt distance: " + tourDistance(twoOptTour));
            System.out.println("Time taken: "  + (System.currentTimeMillis() - startTime));
        }
    }
    /*
    static short[] testOpt(short[] tour){
        for(int i = 0; i < 20; i++){
            twoOptPass(tour);
        }
        short[] newTour;
        for(int i = 0; i < 7; i++){
            newTour = shuffledTour(tour);
        }
        boolean foundBetterTour = true;
        while (foundBetterTour) {
            if (timeLimitPassed())
                return tour;
            foundBetterTour = twoOptPass(tour);
        }
        return tour;
    }
    */

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
        if((y-x) < tour.length/2)
            while (y > x) {
                tmp = tour[y];
                tour[y] = tour[x];
                tour[x] = tmp;
                x++;
                y--;
            }
        else{
            int i = 0;
            int j = x-1;
            short[] temp1 = new short[x];
            while(j>i){
                temp1[j] = tour[i];
                temp1[i] = tour[j];
                i++;
                j--;
            }
            i = 0;
            short[] temp2 = new short[tour.length-y];
            j = temp2.length-1;
            while(j >i){
                tmp = tour[j];
                temp2[j] = tour[i];
                temp2[i] = tmp;
                i++;
                j--;
            }
            for(int k = 0; k < temp1.length; k++){
                tour[k] = temp1[k];
            }
            for(int l = 0; l < temp2.length; l++){
                tour[l] = temp2[l];
            }
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

    private static void calculateDistances(int pointsCount, double[][] coordinates) {
        distances = new double[pointsCount][pointsCount];
        if(numNeighbors > pointsCount){
            numNeighbors = pointsCount;
        }
        //neighborList = new HashMap<Integer, Neighbor[]>(coordinates.length);
        for (int i = 0; i < pointsCount; i++) {
            //for (int j = 0; j < pointsCount; j++) {
            for (int j = 0; j < i; j++) {
                distances[i][j] = coordinateDistance(coordinates[i], coordinates[j]);
                distances[j][i] = distances[i][j];
            }
            /*
            double[] neighborDistances = distances[i];
            // Create neighborlist
            Neighbor[] neighbors = new Neighbor[pointsCount];
            for(int j = 0; j < neighborDistances.length; j++){
                neighbors[j] = new Neighbor(j, neighborDistances[j]);
            }
            Arrays.sort(neighbors);
            neighborList.put(i, Arrays.copyOfRange(neighbors, 0, numNeighbors));
            */
        }
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
