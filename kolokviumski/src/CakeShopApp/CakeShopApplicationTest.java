package CakeShopApp;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Item{
    private String name;
    private int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Item(String part) {
        name = part;
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
}
class Order implements Comparable<Order> {
    private int id;
    List<Item> items;

    public Order(String str) {
        items = new ArrayList<>();
        String[] parts = str.split(" ");
        id = Integer.parseInt(parts[0]);
        for(int i=1; i<parts.length; i+=2){
            items.add(new Item(parts[i], Integer.parseInt(parts[i+1])));
        }
//        Arrays.stream(parts).skip(1)
//                .forEach(part -> {
//                    if(Character.isAlphabetic(part.charAt(0))){
//                        items.add(new Item(part));
//                    }else{
//                        items.getLast().setPrice(Integer.parseInt(part));
//                    }
//                });
    }

    public List<Item> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Order o) {
        return Integer.compare(this.items.size(), o.items.size());
    }
}

class CakeShopApplication{
    List<Order> list;
    public CakeShopApplication() {
        list = new ArrayList<>();
    }

    public int readCakeOrders(InputStream in) {
        //orderId itemName itemPrice...
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        list = br.lines().map(Order::new).collect(Collectors.toList());
        return list.size();
    }

    public void printLongestOrder(OutputStream out) {
        //orderId totalNumberItems
        PrintWriter pw = new PrintWriter(out);
        int max = -1;
        int oId = -1;
        for (Order o : list){
            if(o.getItems().size()>max){
                max = o.getItems().size();
                oId = o.getId();
            }
        }
        pw.println(oId + " " + max);
        pw.flush();
        //Order longest = list.stream().max(Comparator.naturalOrder()).orElse(null);
    }
}

public class CakeShopApplicationTest {
    public static void main(String[] args) {
        CakeShopApplication cakeShopApplication = new CakeShopApplication();
        System.out.println("------READING FROM INPUT STREAM------");
        System.out.println(cakeShopApplication.readCakeOrders(System.in));
        System.out.println("------PRINTING LARGEST ORDER TO OUTPUT STREAM------");
        cakeShopApplication.printLongestOrder(System.out);
    }
}
