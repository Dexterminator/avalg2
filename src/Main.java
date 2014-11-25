import java.io.IOException;

/**
 * Created by dexter on 24/11/14.
 */
public class Main {
    static Kattio io;


    public static void main(String[] args) throws IOException {
        io = new Kattio(System.in, System.out);
        int pointsCount = io.getInt();
        for (int i = 0; i < pointsCount; i++) {
            double coord1 = io.getDouble();
            double coord2 = io.getDouble();
            System.out.println(coord1 + " " + coord2);
        }
    }
}
