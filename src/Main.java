import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static int[][] distances;
    static int TIME_LIMIT = 1000;
    static long startTime;
    static boolean DEBUG = false;
    private static Random random;
    static List<List<Short>> neighborLists;
    static int numNeighbors = 20;


    public static void main(String[] args) throws IOException {
        if (args.length > 0)
            DEBUG = Boolean.parseBoolean(args[0]);
        startTime = System.currentTimeMillis();
        random = new Random();
        // Setup
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        if(pointsCount <= 3){
            for (int i = 0; i < 3; i++) {
                System.out.println(i);
            }
            System.exit(0);
        }
        Point2D.Double[] coordinates = readCoordinates(pointsCount);
        calculateDistances(pointsCount, coordinates);
        neighborLists = getNeighbourLists(pointsCount);

        // Greedy
//        long greedyTime = System.currentTimeMillis();
        short[] greedyTour = ultimateGreedy(pointsCount);
        int greedyTourDistance = -1;
        if (DEBUG) {
//            System.out.println("Greedy time: " + (System.currentTimeMillis() - greedyTime));
            greedyTourDistance = tourDistance(greedyTour);
            /*
            for (short i : greedyTour) {
                System.out.println(i);
            }*/

        }

        // 2-opt

        short[] twoOptTour = twoOpt(greedyTour);
        int bestTourlength = tourDistance(twoOptTour);
        int possibleTourLength;

        while(!timeLimitPassed()){
            short[] possibleTour = Arrays.copyOf(twoOptTour, twoOptTour.length);
            ultimateShuffle(possibleTour);
            twoOpt(possibleTour);
            possibleTourLength = tourDistance(possibleTour);
            if(possibleTourLength < bestTourlength){
                twoOptTour = possibleTour;
                bestTourlength = possibleTourLength;

            }
        }


        printTour(twoOptTour);
        //printTour(twoOptTour);

        /*
//        short[] shuffledTour = shuffledTour(twoOptTour);
//        twoOpt(shuffledTour);
        for (short i : twoOptTour)
            System.out.println(i);
            */
        if (DEBUG) {
            System.out.println("Greedy distance: " + greedyTourDistance);
            System.err.println("2-opt distance: " + bestTourlength);
            System.out.println("Time taken: "  + (System.currentTimeMillis() - startTime));
        }
    }

    static short[] twoOpt(short[] tour) {
        boolean foundBetterTour = ultimateTwoOptPass(tour);

        while (foundBetterTour) {
            if (timeLimitPassed())
                return tour;
            foundBetterTour = ultimateTwoOptPass(tour);
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
                    reverse(tour, i, k);
                    return true;
                }
            }
        }
        return false;
    }

    static boolean ultimateTwoOptPass(short[] tour) {
        int n = tour.length;
        for (short curr = 0; curr < n; curr++) {
            List<Short> neighborList = neighborLists.get(curr);
            short currNeighbor = tour[curr];
            if(timeLimitPassed())
                return false;
            for (short j = 0; j < numNeighbors; j++) {
                short newNeighbor = neighborList.get(j);
                if(currNeighbor == newNeighbor)
                    break;
                short newNeighborsNeighbor = tour[newNeighbor];
                if(currNeighbor == newNeighborsNeighbor)
                    continue;

                int oldDist = distance(curr, currNeighbor) + distance(newNeighbor, newNeighborsNeighbor);
                int newDist = distance(curr, newNeighbor) + distance(currNeighbor, newNeighborsNeighbor);
                if (newDist < oldDist){
                    ultimateTwoOptSwap(tour, curr, newNeighbor);
                    return true;
                }

            }

        }
        return false;
    }

    static void ultimateTwoOptSwap(short[] tour, short currentNode, short newNeighbor){
        short neighborsNeighbor = tour[newNeighbor];
        List<Short> smallTour = new ArrayList<Short>();
        smallTour.add(currentNode);

        short lastInSmall = currentNode;
        while(lastInSmall != neighborsNeighbor){
            lastInSmall = tour[lastInSmall];
            smallTour.add(lastInSmall);
        }
        if(smallTour.size() == 1){
            return;
        }

        Collections.reverse(smallTour.subList(1, smallTour.size()-1));

        for(int i = 0; i < smallTour.size()-1; i++){
            short node1 = smallTour.get(i);
            short node2 = smallTour.get(i+1);
            tour[node1] = node2;
        }
    }

    static void ultimateShuffle(short[] tour){
        int limit =(int) (3.5 - Math.random());
        for(int i = 0; i < limit; i++){
            short edge1node1 = (short) (Math.random() * tour.length);
            short edge1node2 = tour[edge1node1];
            short edge2node1 = edge1node1;
            short edge2node2 = edge1node2;
            while(edge1node1 == edge2node1 || edge2node1 == edge1node2 ||
                    edge2node2 == edge1node1 || edge2node2 == edge1node2){
                edge2node1 = (short) (Math.random() * tour.length);
                edge2node2 = tour[edge2node1];
            }
            ultimateTwoOptSwap(tour, edge1node1, edge2node2);

        }


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
            reverse(tour, mini, mink);
        return foundBetterPath;
    }

    static void twoOptSwap(short[] tour, int x, int y) {
        short tmp;
        if((y-x) > tour.length /2)
            reverse(tour, x, y);
        else{
            int i = 0;
            int j = x-1;
            short[] temp1 = Arrays.copyOfRange(tour, 0, x);
            reverse(temp1, i, j);
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

    static short[] ultimateGreedy(int length) {
        short[] tour = new short[length];
        boolean[] used = new boolean[tour.length];
        short first = (short) random.nextInt(length);
        used[first] = true;
        short current = neighborLists.get(first).get(0);
        tour[first] = current;
        used[current] = true;
        for(int i = 0; i < tour.length-1; i++) {
            List<Short> neighborList = neighborLists.get(current);
            for (short neighbor : neighborList) {
                if (!used[neighbor]) {
                    tour[current] = neighbor;
                    used[neighbor] = true;
                    current = neighbor;
                    break;
                }
            }
        }
        // Close the tour manually
        tour[current] = first;

        return tour;
    }

    private static int tourDistance(short[] tour) {
        int length = 0;


        for (int i = 0; i < tour.length; i++) {
            length += distance(i, tour[i]);

        }
        /*
        while(current != first){
            length += distance(current, previous);
            previous = current;
            current = tour[current];
        }
        */
        /*
        for (int i = 1; i < tour.length; i++) {
            length += distance(tour[i-1], tour[i]);
        }*/
        //length += distance(current, first);
        return length;
    }

    static void printTour(short[] tour) {
        short first = tour[0];
        short current = tour[first];
        for (int i = 0; i < tour.length; i++) {
            io.println(current);
            current = tour[current];
        }
        io.flush();
    }

    private static int distance(int i, int j) {
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

    private static List<List<Short>> getNeighbourLists(int pointsCount) {
        List<List<Short>> neighborLists = new ArrayList<List<Short>>();
        for(short i = 0; i < pointsCount; i++) {
            NodeComparator comp = new NodeComparator(i);
            List<Short> neighborList = getUnsortedNeighbors(i);
            Collections.sort(neighborList, comp);
            neighborLists.add(neighborList);
        }
        return neighborLists;
    }

    private static List<Short> getUnsortedNeighbors(short node) {
        List<Short> unsortedNeighbors = new ArrayList<Short>();
        for (short i = 0; i < distances.length; i++) {
            if (i == node)
                continue;
            unsortedNeighbors.add(i);
        }
        return unsortedNeighbors;
    }

    private static class NodeComparator implements Comparator<Short> {
        private int node;

        public NodeComparator(int node) {
            this.node = node;
        }

        @Override
        public int compare(Short node1, Short node2) {
            return distance(node, node1) - distance(node, node2);
        }
    }

    static boolean timeLimitPassed() {
        return System.currentTimeMillis() - startTime > TIME_LIMIT;
    }
}
