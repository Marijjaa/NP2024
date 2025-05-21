import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class InvalidOperationException extends Exception{
    public InvalidOperationException(String message){
        super(message);
    }
}
abstract class PurchasedProduct implements Comparable<PurchasedProduct>{
    //WS;productID;productName;productPrice;quantity
    private String ID;
    private String productName;
    protected double productPrice;
    protected double quantity;

    public PurchasedProduct(String ID, String productName, double productPrice, double quantity) {
        this.ID = ID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }
    public String getID(){
        return ID;
    }
    abstract public double totalPrice();

    public int compareTo(PurchasedProduct product2) {
        return Double.compare(this.totalPrice(), product2.totalPrice());
    }
}
class PS extends PurchasedProduct{

    public PS(String ID, String productName, double productPrice, double quantity) {
        super(ID, productName, productPrice, quantity);
    }

    @Override
    public double totalPrice() {
        return (double) (quantity/1000) * productPrice;    //quantiti vo gramovi
    }

}
class WS extends PurchasedProduct{

    public WS(String ID, String productName, double productPrice, double quantity) {
        super(ID, productName, productPrice, quantity);
    }

    @Override
    public double totalPrice() {
        return productPrice*quantity;
    }
}

class ShoppingCart{
    List<PurchasedProduct> products;

    public ShoppingCart() {
        products=new ArrayList<>();
    }

    public void addItem(String s) throws InvalidOperationException1 {
        String[] parts = s.split(";");
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        double price = Double.parseDouble(parts[3]);
        double quantity = Double.parseDouble(parts[4]);
        if (quantity==0.0){
            throw new InvalidOperationException1(String.format("The quantity of the product with id %s can not be 0.", id));
        }
        if (type.equals("WS")){
            products.add(new WS(id,name,price,quantity));
        }else {
            products.add(new PS(id,name,price,quantity));
        }
    }

    public void blackFridayOffer(List<Integer> discountItems, PrintStream out) throws InvalidOperationException1 {
        if(discountItems.isEmpty()){
            throw new InvalidOperationException1("There are no products with discount.");
        }
        PrintWriter pw = new PrintWriter(out);
        for(PurchasedProduct pp : products){
            if(discountItems.contains(Integer.parseInt(pp.getID()))){
                double oldPrice = pp.productPrice;
                double oldTotal = pp.totalPrice();
                pp.productPrice= oldPrice*0.9;
                pw.println(String.format("%s - %.2f", pp.getID(), oldTotal-pp.totalPrice()));
            }
        }
        pw.flush();
    }

    public void printShoppingCart(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        products.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(product -> pw.printf("%s - %.2f", product.getID(), product.totalPrice()));
        pw.flush();
    }
}
public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}