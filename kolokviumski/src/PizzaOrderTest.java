import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

class InvalidExtraTypeException extends Exception{
    public InvalidExtraTypeException(String message){
        super(message);
    }
}
class InvalidPizzaTypeException extends Exception{
    public InvalidPizzaTypeException(String message){
        super(message);
    }
}
class ItemOutOfStockException extends Exception{
    public ItemOutOfStockException(String message){
        super(message);
    }
}

class EmptyOrder extends Exception{
    public EmptyOrder(String message){
        super(message);
    }
}
class OrderLockedException extends Exception{
    public OrderLockedException(String message){
        super(message);
    }
}
class ArrayIndexOutOfBоundsException extends Exception{
    public ArrayIndexOutOfBоundsException(String message){
        super(message);
    }
}

interface Item{
    int getPrice();

    String getType();
}
class ExtraItem implements Item{
    String type;

    public ExtraItem(String type) throws InvalidExtraTypeException {
        if(type.equals("Coke") || type.equals("Ketchup")) {
            this.type = type;
        }else{
            throw new InvalidExtraTypeException("InvalidExtraTypeException");
        }
    }

    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        if(type.equals("Ketchup")){
            return 3;
        }
        return 5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraItem extraItem = (ExtraItem) o;
        return Objects.equals(type, extraItem.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
class PizzaItem implements Item{
    String type;

    public PizzaItem(String type) throws InvalidPizzaTypeException {
        if(type.equals("Standard") || type.equals("Pepperoni") || type.equals("Vegetarian")) {
            this.type = type;
        }else{
            throw new InvalidPizzaTypeException("InvalidPizzaTypeException");
        }
    }
    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        if(type.equals("Standard")){
            return 10;
        } else if (type.equals("Pepperoni")){
            return 12;
        }
        return 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PizzaItem pizzaItem = (PizzaItem) o;
        return Objects.equals(type, pizzaItem.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
class Instance{
    Item item;
    int count;

    public Instance(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
class Order{
    List<Instance> instances;
    boolean locked = false;
    public Order(){
        instances=new ArrayList<>();
    }
    public void addItem(Item item, int count) throws ItemOutOfStockException, OrderLockedException {
        if(locked){
            throw new OrderLockedException("OrderLockedException");
        }
        if(count>10) throw new ItemOutOfStockException(String.format("Item %s Out Of Stock Exception", item.getType()));
        for (Instance i : instances) {
            if(i.getItem().equals(item)){
                i.setCount(count);
                return;
            }
        }
        instances.add(new Instance(item, count));
    }
    public void removeItem(int i) throws OrderLockedException, ArrayIndexOutOfBоundsException {
        if(i<0 || i>instances.size()){
            throw new ArrayIndexOutOfBоundsException("ArrayIndexOutOfBоundsException");
        }
        if(locked){
            throw new OrderLockedException("OrderLockedException");
        }
        instances.remove(i);
    }
    public void lock() throws EmptyOrder {
        if(instances.isEmpty()){
            throw new EmptyOrder("EmptyOrder");
        }
        locked=true;
    }
    public void displayOrder(){
        StringBuilder sb = new StringBuilder();

        instances.stream().forEach(i -> sb.append(String.format("%3d.%-15sx%2d%5d$%n", instances.indexOf(i) + 1, i.getItem().getType(), i.getCount(), i.getItem().getPrice() * i.getCount())));
        sb.append(String.format("%-22s%5d$", "Total:", getPrice()));
        System.out.println(sb);
    }

    public int getPrice() {
        return instances.stream()
                .mapToInt(i -> i.getItem().getPrice()*i.getCount())
                .sum();
    }
}

public class PizzaOrderTest {
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }
}