import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Book implements Comparable<Book>{
    String title;
    String category;
    float price;

    public Book(String title, String category, float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %.2f", title, category, price);
    }

    @Override
    public int compareTo(Book o) {
        int res = this.title.compareTo(o.title);
        if (res == 0){
            return Float.compare(this.price, o.price);
        }else
            return res;
    }
}
class BookCollection{
    List<Book> books;

    public BookCollection() {
        books = new ArrayList<>();
    }
    public void addBook(Book book){
        books.add(book);
    }
    public void printByCategory(String category){
        books.stream()
                .filter(book -> book.category.equalsIgnoreCase(category))
                .sorted()
                .forEach(System.out::println);
    }
    public void getCheapestN(int n){
        books.stream()
                .sorted(Comparator.comparing(book -> book.price))
                .limit(n)
                .forEach(System.out::println);
    }
}
public class BookTest {
    public static void main(String[] args) {
        Book book1 = new Book("Doktor Ofboli", "roman", 200);
        Book book2 = new Book("Doktor Ofboli", "roman", 500);
        Book book3 = new Book("Shagrinska", "roman", 250);
        BookCollection bc = new BookCollection();
        bc.addBook(book1);
        bc.addBook(book2);
        bc.addBook(book3);
        bc.printByCategory("roman");
        bc.getCheapestN(2);
    }

}
