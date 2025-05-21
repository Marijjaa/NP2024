import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Proba {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int distance = sc.nextInt();
        List<Float> earnings = new ArrayList<>();
        int baseFee = 90;
        int additionalFee = (int) (distance / 10.0) * 10;
        earnings.add((float) (baseFee + additionalFee));
        System.out.println(earnings.stream().mapToDouble(Float::doubleValue).sum());
    }
}
