import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class AmountNotAllowedException extends Exception{
    public AmountNotAllowedException(int amount){
        super(String.format("Receipt with amount %d is not allowed to be scanned", amount));
    }
}

abstract class Product{
    double item_price;

    public Product(double item_price) {
        this.item_price = item_price;
    }
    public int getPrice(){
        return (int)item_price;
    }

    public abstract double tax();
}
class AProduct extends Product{

    public AProduct(double item_price) {
        super(item_price);
    }

    @Override
    public double tax() {
        return item_price*0.18;
    }
}
class BProduct extends Product{

    public BProduct(double item_price) {
        super(item_price);
    }

    @Override
    public double tax() {
        return item_price*0.05;
    }
}
class VProduct extends Product{

    public VProduct(double item_price) {
        super(item_price);
    }

    @Override
    public double tax() {
        return 0;
    }
}
class Check{
    String id;
    List<Product> products;
    double sum;
    public Check(String s){
        products = new ArrayList<>();
        String[] parts = s.split(" ");
        id = parts[0];
        for(int i=1; i<parts.length; i+=2){
            if (parts[i + 1].equals("A")){
                products.add(new AProduct(Double.parseDouble(parts[i])));
            } else if (parts[i + 1].equals("B")) {
                products.add(new BProduct(Double.parseDouble(parts[i])));
            }else {
                products.add(new VProduct(Double.parseDouble(parts[i])));
            }
        }
    }

    public int sum() {
        return products.stream()
                .mapToInt(Product::getPrice)
                .sum();
    }

    public double total_tax(){
        return products.stream()
                .mapToDouble(product -> product.tax()*0.15)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f", id, sum(), total_tax());
    }
}

class MojDDV{
    List<Check> checks;
    public MojDDV() {
        checks=new ArrayList<>();
    }

    public void readRecords(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        checks = br.lines().map(line -> {
            try {
                Check check = new Check(line);
                if (check.sum() > 30000) {
                    throw new AmountNotAllowedException(check.sum());
                }
                return check;
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void printTaxReturns(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        checks.stream().forEach(pw::println);
        pw.flush();
    }

    public void printStatistics(PrintStream out) {
        if (checks.isEmpty()) {
            return;
        }

        List<Double> taxReturns = checks.stream()
                .map(Check::total_tax)
                .collect(Collectors.toList());

        double min = taxReturns.stream().min((num1,num2) -> num1.compareTo(num2)).orElse(0.0);
        double max = taxReturns.stream().max(Double::compare).orElse(0.0);
        double sum = taxReturns.stream().mapToDouble(Double::doubleValue).sum();
        double average = sum / taxReturns.size();

        out.printf("min:\t%.3f\n", min);
        out.printf("max:\t%.3f\n", max);
        out.printf("sum:\t%.3f\n", sum);
        out.printf("count:\t%-5d\n", taxReturns.size());
        out.printf("avg:\t%.3f\n", average);
    }
}

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);
    }
}