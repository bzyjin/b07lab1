import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Polynomial {
    double[] c;
    int[] e;

    public Polynomial() {
        this.c = null;
        this.e = null;
    }

    public Polynomial(double[] c, int[] e) {
        this();
        if (c != null && e != null) {
            assert c.length == e.length;

            this.c = c.clone();
            this.e = e.clone();
        }
    }

    public Polynomial(String s) {
        s = s   .replaceAll("-", "+-")
                .replaceAll("\\+x", "+1x")
                .replaceAll("-x", "-1x")
                .replaceAll("x", "x ");
        if (s.startsWith("x")) s = "1" + s;
        String[] tokens = s.split("\\+");

        int len = tokens.length;
        double[] c = new double[len];
        int[] e = new int[len];

        for (int i = 0; i < len; ++i) {
            String[] constants = tokens[i].split("x");

            c[i] = Double.parseDouble(constants[0]);
            if (constants.length < 2) e[i] = 0;
            else if (constants[1].equals(" ")) e[i] = 1;
            else e[i] = Integer.parseInt(constants[1].trim());
        }

        this.c = c;
        this.e = e;
    }

    public Polynomial(File file) throws FileNotFoundException {
        this((new Scanner(file)).nextLine());
    }

    public void saveToFile(String path) throws IOException {
        FileWriter f = new FileWriter(path);

        if (this.len() == 0) {
            f.write("0");
        }

        else {
            StringBuilder fmt = new StringBuilder();
            for (int i = 0; i < this.len(); ++i) {
                if (i > 0 && this.c[i] >= 0) fmt.append("+");
                fmt.append(String.valueOf(this.c[i]));

                if (this.e[i] > 0) {
                    fmt.append("x");
                    if (this.e[i] > 1) {
                        fmt.append(String.valueOf(this.e[i]));
                    }
                }
            }

            f.write(fmt.toString());
        }

        f.close();
    }

    private Polynomial addUnsafe(Polynomial that) {
        if (that.len() == 0) return new Polynomial(this.c, this.e);
        if (this.len() == 0) return new Polynomial(that.c, that.e);

        int len = this.len() + that.len();
        int[] e = new int[len];
        double[] c = new double[len];

        Polynomial  a = this, b = that;

        int i = 0, p2 = 0;
        for (int p1 = 0; p1 < a.len(); ++p1) {
            while (p2 < b.len() && b.e[p2] <= a.e[p1]) {
                c[i] += b.c[p2];
                e[i++] = b.e[p2++];
            }

            if (p2 > 0 && b.e[p2 - 1] == a.e[p1]) --i;

            c[i] += a.c[p1];
            e[i++] = a.e[p1];
        }

        while (p2 < b.len()) {
            c[i] += b.c[p2];
            e[i++] += b.e[p2++];
        }

        return new Polynomial(c, e)
                .withFilteredTerms();
    }

    public Polynomial add(Polynomial that) {
        return this.ordered().addUnsafe(that.ordered());
    }

    public Polynomial subtract(Polynomial that) {
        return this.add(that.negated());
    }

    public Polynomial withFilteredTerms() {
        if (this.len() == 0) return new Polynomial();

        int numZeros = 0;
        for (int i = 0; i < this.c.length; ++i) {
            if (Math.abs(this.c[i]) <= 1e-9) ++numZeros;
        }

        double[] c = new double[this.len() - numZeros];
        int[] e = new int[c.length];

        if (c.length == 0) return new Polynomial();

        int j = 0;
        for (int i = 0; i < this.c.length; ++i) {
            if (Math.abs(this.c[i]) <= 1e-9) continue;

            c[j] = this.c[i];
            e[j] = this.e[i];
            ++j;
        }

        return new Polynomial(c, e);
    }

    public Polynomial negated() {
        if (this.len() == 0) return new Polynomial();

        double[] c = this.c.clone();
        for (int i = 0; i < c.length; ++i) {
            c[i] *= -1;
        }

        return new Polynomial(c, this.e);
    }

    public Polynomial multiply(Polynomial that) {
        if (this.len() == 0 || that.len() == 0) return new Polynomial();

        Polynomial res = new Polynomial();
        Polynomial  a = this.ordered(),
                    b = that.ordered();

        for (int i = 0; i < this.e.length; ++i) {
            int[] e = b.e.clone();
            double[] c = b.c.clone();

            for (int j = 0; j < e.length; ++j) {
                e[j] += a.e[i];
                c[j] *= a.c[i];
            }

            res = res.addUnsafe(new Polynomial(c, e));
        }

        return res;
    }

    public Polynomial ordered() {
        if (this.len() == 0) return new Polynomial();

        double[] c = new double[this.c.length];
        int[] e = new int[this.e.length];

        // Insertion sort
        for (int i = 0; i < this.len(); ++i) {
            int j = i;

            double c_val = this.c[i];
            int e_val = this.e[i];

            while (j > 0 && e[j - 1] > e_val) {
                c[j] = c[j - 1];
                e[j] = e[j - 1];
                --j;
            }

            c[j] = c_val;
            e[j] = e_val;
        }

        return new Polynomial(c, e);
    }

    public double evaluate(double x) {
        if (this.len() == 0) return 0.;
        double res = 0.;

        for (int i = 0; i < this.len(); ++i) {
            res += this.c[i] * Math.pow(x, this.e[i]);
        }

        return res;
    }

    public boolean hasRoot(double x) {
        return Math.abs(this.evaluate(x)) <= 1e-9; // EPSILON
    }

    public int len() {
        if (this.c == null || this.e == null) return 0;
        return this.c.length;
    }
}
