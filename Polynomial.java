public class Polynomial {
    double[] coefficients;

    public Polynomial() {
        this.coefficients = new double[1];
    }

    public Polynomial(double[] coefficients) {
        this.coefficients = new double[coefficients.length];
        for (int i = 0; i < coefficients.length; ++i) {
            this.coefficients[i] = coefficients[i];
        }
    }

    public Polynomial add(Polynomial that) {
        boolean thisLarger = this.coefficients.length > that.coefficients.length;
        double[] large = thisLarger ? this.coefficients : that.coefficients;
        double[] small = thisLarger ? that.coefficients : this.coefficients;

        Polynomial res = new Polynomial(large);
        for (int i = 0; i < small.length; ++i) {
            res.coefficients[i] += small[i];
        }

        return res;
    }

    public double evaluate(double x) {
        double power = 1.;
        double res = 0.;

        for (int i = 0; i < this.coefficients.length; ++i) {
            res += power * this.coefficients[i];
            power *= x;
        }

        return res;
    }

    public boolean hasRoot(double x) {
        return Math.abs(this.evaluate(x)) <= 1e-9; // EPSILON
    }
}
