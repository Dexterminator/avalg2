/**
 * Created by Ludde on 2014-12-01.
 */
public class Node {
    public int to;
    public int from;
    public double[] distanceList;
    public int nodeNum;

    public Node(double[] distanceList, int nodeNum){
        this.distanceList = distanceList;
    }

    public void reverse (){
        int tmp = to;
        to = from;
        from = tmp;
    }

    public double distanceTo(int node){
        return distanceList[node];
    }
}
