import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static int[][] distances;
    static int TIME_LIMIT = 1400;
    static long startTime;
    static boolean DEBUG = false;
    private static Random random;
    static int numNeighbors = 20;
    static HashMap<Integer, Neighbor[]> neighborList = new HashMap<Integer, Neighbor[]>();

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
        //short[] twoOptTour = twoOpt(greedyTour);
//        short[] shuffledTour = shuffledTour(twoOptTour);
//        twoOpt(shuffledTour);
        /*
        if(DEBUG) {
            for(int i = 0; i < neighborList.size(); i++){
                System.out.println(i + ": " + Arrays.toString(neighborList.get(i)));
            }
        }*/
        short[] twoOptTour = new short[pointsCount];
        while(!timeLimitPassed()){
            twoOptTour = twoOpt(greedyTour, 50);
            twoOptTour = twoOpt(greedyTour, 30);
            twoOptTour = twoOpt(greedyTour, 0);


        }


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
        short[] bestTour;
        for(int i = 0; i < 17; i++){
            twoOptPass(tour);
        }
        bestTour = tour;
        short[] newTour = new short[tour.length];
        newTour = shuffledTour(tour);
        boolean foundBetterTour = true;
        while (foundBetterTour) {
            if (timeLimitPassed())
                return tour;
            foundBetterTour = twoOptPass(newTour);
            if(tourDistance(newTour) < tourDistance(bestTour)){
                bestTour = newTour;
                System.out.println("swag");
            }
        }
        return bestTour;
    }
    */


    static short[] twoOpt(short[] tour, int limit) {
        boolean foundBetterTour = twoOptOnlyNearest(tour, limit);
        int i = 0;
        while (foundBetterTour) {
            if (timeLimitPassed())
                return tour;
            foundBetterTour = twoOptOnlyNearest(tour, limit);
            i++;
        }
        if(DEBUG)
            System.out.println("Iterations: " + i);
        return tour;
    }

    static boolean twoOptPass(short[] tour, int limit) {
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
                if ((oldDist-limit) > newDist) {
                    twoOptSwap(tour, i, k);
                    return true;
                }
            }
        }
        return false;
    }

    static boolean twoOptOnlyNearest(short[] tour, int limit){
        int n = tour.length;
        for (int i = 0; i < tour.length; i++) {
            short current = tour[(i+1) % n];
            Neighbor[] list = neighborList.get((int) tour[i]);
            for (int k = 0; k < list.length && k < numNeighbors; k++) {
                if (timeLimitPassed())
                    return false;
                short neighbor = tour[k];
                if(neighbor == current)
                    break;
                short neighborsNeighbor = tour[(k+1) % n];
                if(i == (k+1) % n){
                    continue;
                }

                if (i == 0 && (k + 1) == n)
                    continue;
                //int j = tour[i];

                double oldDist = distance(tour[i], current) + distance(neighbor, neighborsNeighbor);
                double newDist = distance(tour[i], neighbor) + distance(current, neighborsNeighbor);
                if ((oldDist-limit) > newDist) {
                    twoOptSwap(tour, i, k);
                    //neighborList.get(jMinus)[j] = lNeighbor;
                    //neighborList.get(j)[lPlus] = lPlusNeighbor;

                    break;
                }
            }
        }
        return false;
    }


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
        // TODO: check if x>y is necessary
        if((y-x) > tour.length /2 || x>y)
            reverse(tour, x, y);
        else{
            /*
            Reverse the compliment if this takes less time. Such optimization.
             */
            int i = 0;
            int j = x-1;
            short[] temp1 = Arrays.copyOfRange(tour, 0, x);
            reverse(temp1, i, j);
            //System.out.println(x + ", " + y);
            short[] temp2 = Arrays.copyOfRange(tour, x, y+1);
            i = 0;

            short[] temp3 = Arrays.copyOfRange(tour, y+1, tour.length);
            j = temp3.length-1;
            reverse(temp3, i, j);
            for(int l = 0; l < temp3.length; l++){
                tour[l] = temp3[l];
            }
            int p = 0;
            for(int k = temp3.length; k < temp3.length+y+1-temp1.length; k++){
                tour[k] = temp2[p];
                p++;
            }
            p =  y+1+temp3.length-temp1.length;
            for(int k = 0; k < temp1.length; k++){
                tour[p] = temp1[k];
                p++;
            }


        }
    }

    static void reverse(short[] tour, int x, int y){
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
            reverse(tour, index1, index2);
        }
        return shuffledTour;
    }

    static short[] newShuffledTour(short[] tour) {
        short[] shuffledTour = Arrays.copyOf(tour, tour.length);
        int index1 = random.nextInt(tour.length);
        int index2 = random.nextInt(tour.length);
        short temp1 = tour[index1];
        tour[index1] = tour[index2];
        tour[index2] = temp1;
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
            for(int j = 0; j< pointsCount; j++){
            //for(int j = 0; j <= i; j++) {
                if(distances[i][j] != 0.0)
                    continue;
                int dist = (int) Math.round(coordinates[i].distance(coordinates[j]));
                distances[i][j] = dist;
                distances[j][i] = dist;
            }
            Neighbor[] neighbors = new Neighbor[pointsCount];
            for(int j = 0; j<pointsCount; j++){
                neighbors[j] = new Neighbor(j, distances[i][j]);
            }
            Arrays.sort(neighbors);
            neighborList.put(i, neighbors);
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
