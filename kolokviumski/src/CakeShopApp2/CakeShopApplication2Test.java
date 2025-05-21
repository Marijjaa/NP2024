package CakeShopApp2;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


class InvalidOrderException extends Exception{
    public InvalidOrderException(int id) {
        super(String.format("The order with id %d has less items than the minimum allowed", id));
    }
}
abstract class Item{
    protected String name;
    protected int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Item(String part) {
        name = part;
        price = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public abstract Type getType();
}
class Cake extends Item{

    public Cake(String part) {
        super(part);
    }

    @Override
    public Type getType() {
        return Type.CAKE;
    }
}
class Pie extends Item{

    public Pie(String part) {
        super(part);
    }

    @Override
    public Type getType() {
        return Type.PIE;
    }
    @Override
    public int getPrice() {
        return price+50;
    }

}

class Order implements Comparable<Order> {
    private int id;
    List<Item> items;

    public Order(int orderId, List<Item> itemsList) {
        id = orderId;
        items = itemsList;
    }

    public static Order createOrder(String str, int minOrderItems) throws InvalidOrderException {
        List<Item> itemsList = new ArrayList<>();
        String[] parts = str.split(" ");
        int orderId = Integer.parseInt(parts[0]);

        Arrays.stream(parts).skip(1)
                .forEach(part -> {
                    if(Character.isAlphabetic(part.charAt(0))){
                        if (part.charAt(0) == 'c')
                            itemsList.add(new Cake(part));
                        else
                            itemsList.add(new Pie(part));
                    }else{
                        itemsList.getLast().setPrice(Integer.parseInt(part));
                    }
                });
        if (itemsList.size()<minOrderItems)
            throw new InvalidOrderException(orderId);
        return new Order(orderId, itemsList);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }
    public int totalPies(){
        return (int) items.stream()
                .filter(item -> item.getType().equals(Type.PIE))
                .count();
    }
    public int totalCakes(){
        return (int) items.stream()
                .filter(item -> item.getType().equals(Type.CAKE))
                .count();
    }
    public int getSum(){
        return items.stream()
                .mapToInt(Item::getPrice)
                .sum();
    }

    @Override
    public String toString() {
        return id + " " + items.size()  + " " + totalPies() + " " + totalCakes() + " " + getSum();
    }

    @Override
    public int compareTo(Order o) {
        return Integer.compare(this.getSum(), o.getSum());
    }
}
class CakeShopApplication{
    int minOrderItems;
    List<Order> orders;
    public CakeShopApplication(int minOrderItems) {
        orders = new ArrayList<>();
        this.minOrderItems = minOrderItems;
    }

    public void readCakeOrders(InputStream in) {
        //orderId itemName itemPrice...
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        orders = br.lines()
                .map(str -> {
                    try {
                        return Order.createOrder(str, minOrderItems);
                    } catch (InvalidOrderException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printAllOrders(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        orders.stream().sorted(Comparator.reverseOrder()).forEach(order -> pw.println(order.toString()));
        pw.flush();
    }
}
public class CakeShopApplication2Test {

    public static void main (String[]args){
        CakeShopApplication cakeShopApplication = new CakeShopApplication(4);
        System.out.println("------READING FROM INPUT STREAM------");
        cakeShopApplication.readCakeOrders(System.in);
        System.out.println("------PRINTING LARGEST ORDER TO OUTPUT STREAM------");
        cakeShopApplication.printAllOrders(System.out);
    }
}