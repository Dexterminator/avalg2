import java.util.Arrays;

/**
 * Created by Ludde on 2014-11-27.
 */
public class TwoOpt {
    private static final long TIME_LIMIT = 10;
    /*
        The 2-opt algorithm
     */
    public static Node[] twoOpt(Node[] tour, double[][] coordinates, Node[] nodeList){
        Node[] res = tour;
        System.out.println(Arrays.toString(tour));
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis()-startTime) < TIME_LIMIT){
            for(int i = 0; i < tour.length; i++){
                for(int k = i+1; k < tour.length; k++){
                    int next1 = i+1;
                    int next2 = k+1;
                    double dist1 = nodeList[i].distanceTo(next1);
                    /* Handle last element in tour */
                    if(next2 == tour.length)
                        next2 = 0;
                    dist1 += nodeList[k].distanceTo(next2);
                    double dist2 = nodeList[i].distanceTo(k);
                    dist2 += nodeList[next1].distanceTo(next2);
                    if(dist2 > dist1){
                        res = twoOptSwap(res, i, k);

                    }
                }

            }
        }
        return res;
    }

    public static short[] twoOptSwap(short[] tour, int i, int k){
        short[] newTour = new short[tour.length];
        // Add edges 0 to i-1 to new tour in order
        /*
        for(int j = 0; j < i; j++){
            newTour[j] = tour[j];
        }*/
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


}
