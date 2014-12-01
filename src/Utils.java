/**
 * Created by Ludde on 2014-12-01.
 */
public class Utils {
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
