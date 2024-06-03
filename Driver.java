import java.io.File;

public class Driver {
    public static void main(String [] args) {
        Polynomial p = new Polynomial();
        System.out.println(p.evaluate(3));

        double[] c1 = {6, 5};
        int[] e1 = {0, 3};
        Polynomial p1 = new Polynomial(c1, e1);

        double[] c2 = {-2, -9};
        int[] e2 = {1, 4};
        Polynomial p2 = new Polynomial(c2, e2);

        Polynomial s = p1.add(p2);
        System.out.println("s(0.1) = " + s.evaluate(0.1));

        if(s.hasRoot(1))
            System.out.println("1 is a root of s");
        else
            System.out.println("1 is not a root of s");

        p = p2.multiply(p1);

        try {
            File poly = new File("poly.txt");
            Polynomial g = new Polynomial(poly);
            System.out.println("Loaded poly.txt");
        } catch (Exception e) {
            System.out.println("Could not load poly.txt");
        }

        try {
            p.saveToFile("out.txt");
            System.out.println("Saved p to out.txt");
        } catch (Exception e) {
            System.out.println("Could not save p to out.txt");
        }
    }
}
