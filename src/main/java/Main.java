import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Specify the path to the fingerprint image file");
        String path = scan.next();
        double dimension = MinkowskiDimension.getDimension(path);
        System.out.println("Minkowski dimension for the given image = " + dimension);
    }

}
