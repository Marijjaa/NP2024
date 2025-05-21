import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class OnlinePayment {
    String id;
    String item;
    int price;

    public OnlinePayment(String id, String item, int price) {
        this.id = id;
        this.item = item;
        this.price = price;
    }

    @Override
    public String toString() {
        return item + " " + price;
    }

    public int getPrice() {
        return price;
    }
}

class OnlinePayments {
    Map<String, List<OnlinePayment>> onlinePaymentsPerStudent;

    public OnlinePayments() {
        this.onlinePaymentsPerStudent = new HashMap<>();
    }

    public void readItems(InputStream in) {
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine()) {
            String[] parts = sc.nextLine().split(";");
            onlinePaymentsPerStudent.putIfAbsent(parts[0], new ArrayList<>());
            onlinePaymentsPerStudent.get(parts[0]).add(new OnlinePayment(parts[0], parts[1], Integer.parseInt(parts[2])));
        }
    }

    public void printStudentReport(String id, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        if (onlinePaymentsPerStudent.get(id) == null){
            pw.println("Student " + id + " not found!");
            pw.flush();
            return;
        }
        List<OnlinePayment> list = onlinePaymentsPerStudent.get(id).stream().sorted(Comparator.comparing(OnlinePayment::getPrice).reversed()).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        int net = list.stream().mapToInt(onlinePayment -> onlinePayment.price).sum();
        int fee;
        if (net*0.0114>300){
            fee = 300;
        }else if(net*0.0114<3){
            fee = 3;
        } else {
            fee = (int) Math.round(net*0.0114);
        }
        int total = net + fee;
        sb.append(String.format("Student: %s Net: %d Fee: %d Total: %d%nItems:%n", id, net, fee, total));
        AtomicInteger i = new AtomicInteger(1);
        for (OnlinePayment onlinePayment : list) {
            sb.append(i.getAndIncrement()).append(". ").append(onlinePayment).append("\n");
        }
        pw.printf(sb.toString());
        pw.flush();
    }
}

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}