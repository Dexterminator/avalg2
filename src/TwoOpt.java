import java.util.Arrays;

/**
 * Created by Ludde on 2014-11-27.
 */
public class TwoOpt {
    private static final long TIME_LIMIT = 800;
    /*
        The 2-opt algorithm
     */
    public static short[] twoOpt(short[] tour, double[][] coordinates){
        short[] res = tour;
        double tourLength = tourDistance(tour, coordinates);
        /* While no improvement is made, change edges in tour */
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis()-startTime) < TIME_LIMIT){
            for(int i = 0; i < tour.length; i++){
                for(int k = i+1; k < tour.length; k++){
                    short[] newRoute = twoOptSwap(res, i, k);
                    double newLength = tourDistance(newRoute, coordinates);
                    if(newLength < tourLength){
                        tourLength = newLength;
                        res = newRoute;
                    }
                }

            }
        }
        return res;
    }

    public static short[] twoOptSwap(short[] tour, int i, int k){
        short[] newTour = new short[tour.length];
        // Add edges 0 to i-1 to new tour in order
        for(int j = 0; j < i; j++){
            newTour[j] = tour[j];
        }
        // Add edges i to k to new tour in reverse order
        short[] tempTour = Arrays.copyOfRange(tour, i, k+1);
        for(int j = i; j <= tempTour.length / 2; j++){
            short temp = tour[j];
            newTour[j] = tempTour[tempTour.length-j-1];
            newTour[tempTour.length-j-1] = temp;
        }
        // Add edges k+1 to end and add to new tour
        for(int j = k+1; j < tour.length; j++){
            newTour[j] = tour[j];
        }

        return newTour;
    }

    static double distance(double[] coordinate1, double[] coordinate2) {
        double paren1 = Math.pow(coordinate1[0] - coordinate2[0], 2);
        double paren2 = Math.pow(coordinate1[1] - coordinate2[1], 2);
        double distance = Math.sqrt(paren1 + paren2);
        return distance;
    }

    static double tourDistance(short[] tour, double[][] coordinates) {
        double totalDistance = 0;
        for (int i = 1; i < tour.length; i++) {
            double[] coordinate1 = coordinates[tour[i-1]];
            double[] coordinate2 = coordinates[tour[i]];
            /* Make sure no coordinates can be included double */
            if(coordinate2 == coordinate1){
                return Integer.MAX_VALUE;
            }
            totalDistance += distance(coordinate1, coordinate2);
        }
        double[] coordinate1 = coordinates[tour[0]];
        double[] coordinate2 = coordinates[tour[tour.length-1]];
        totalDistance += distance(coordinate1, coordinate2);
        return totalDistance;
    }


}
