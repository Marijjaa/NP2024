import java.util.Scanner;

class ZeroDenominatorException extends Exception{
    public ZeroDenominatorException(String message){
        super(message);
    }
}

class GenericFraction<T extends Number, U extends Number>{
    T numerator;
    U denominator;
    public GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
        if (denominator.doubleValue()==0.0){
            throw new ZeroDenominatorException("Denominator cannot be zero");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }
    public GenericFraction<Double, Double> add(GenericFraction<? extends Number, ? extends Number> gf) throws ZeroDenominatorException {
        double commonDenominator = this.denominator.doubleValue() * gf.denominator.doubleValue();
        double newNumerator = this.numerator.doubleValue() * gf.denominator.doubleValue()
                + gf.numerator.doubleValue() * this.denominator.doubleValue();
        return new GenericFraction<>(newNumerator, commonDenominator);
    }

    public Double toDouble() {
        return numerator.doubleValue()/denominator.doubleValue();
    }

    private double gcd(double a, double b) {
        while (b != 0) {
            double temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    @Override
    public String toString() {
        double gcd = gcd(numerator.doubleValue(), denominator.doubleValue());
        double simplifiedNumerator = numerator.doubleValue() / gcd;
        double simplifiedDenominator = denominator.doubleValue() / gcd;
        return String.format("%.2f / %.2f", simplifiedNumerator, simplifiedDenominator);
    }
}

public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<Double, Double>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<Float, Float>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<Integer, Integer>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<Integer, Integer>(n3, 0);
        } catch(ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

}

// вашиот код овде
