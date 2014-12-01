import java.io.IOException;
import java.util.Arrays;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;
    static double[][] distanceMatrix;
    public static void main(String[] args) throws IOException {
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        distanceMatrix = new double[pointsCount][pointsCount];
        double[][] coordinates = new double[pointsCount][2];
        Node[] nodeList = new Node[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            coordinates[i][0] = io.getDouble();
            coordinates[i][1] = io.getDouble();
        }
        /*
        TODO: Is this a bottleneck...? #yolo
         */
        for(int i = 0; i < nodeList.length; i++){
            double[] distanceArray = new double[nodeList.length];
            for(int j = 0; j < nodeList.length; j++){
                distanceArray[j] = Utils.distance(coordinates[i], coordinates[j]);
            }
            nodeList[i] = new Node(distanceArray, i);
        }
        Node[] greedyTour = greedyTour(nodeList, coordinates);
        /*
        for (short i : greedyTour) {
            io.println(i);
        }
        */
        //System.err.println("Distance: " + TwoOpt.tourDistance(greedyTour, coordinates));

        Node[] twoOptTour = TwoOpt.twoOpt(greedyTour, coordinates, nodeList);
        for (Node i : twoOptTour) {
            io.println(i.nodeNum);
        }
        io.flush();

        //System.err.println("Distance: " + TwoOpt.tourDistance(twoOptTour, coordinates));
    }

    static Node[] greedyTour(Node[] nodeList, double[][]coordinates) {
        Node[] tour = new Node[nodeList.length];
        boolean[] used = new boolean[nodeList.length];
        tour[0] = nodeList[0];
        used[0] = true;
        for (int i = 1; i < nodeList.length; i++) {
            short best = -1;
            double bestDist = Double.MAX_VALUE;
            for (short j = 0; j < nodeList.length; j++) {
                double currDist = nodeList[i].distanceTo(j);
                if (!used[j] && (best == -1 || currDist <
                        bestDist)) {
                    best = j;
                    bestDist = currDist;
                }
            }
            tour[i] = nodeList[best];
            used[best] = true;
        }
        /*
        Handle first and last in tour
        */
        int firstNull = 0;
        int secondNull = 0;

        return tour;
    }
}
