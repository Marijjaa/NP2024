import com.sun.nio.sctp.AbstractNotificationHandler;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Driver {
    String name;
    LocalTime lap1;
    LocalTime lap2;
    LocalTime lap3;

    public Driver(String name, String lap1, String lap2, String lap3) {
        this.name = name;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss:SSS");
        this.lap1 = LocalTime.parse(lap1, formatter);
        this.lap2 = LocalTime.parse(lap2, formatter);
        this.lap3 = LocalTime.parse(lap3, formatter);
    }

    public LocalTime getBestTime() {
        return lap1.isBefore(lap2) ? (lap1.isBefore(lap3) ? lap1 : lap3) : (lap2.isBefore(lap3) ? lap2 : lap3);
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, getBestTime());
    }
}

class F1Race {
    List<Driver> driverList;

    public F1Race() {
        driverList = new ArrayList<>();
    }

    public void readResults(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            // Driver_name lap1 lap2 lap3
            String[] parts = line.split("\\s+");
            String name = parts[0];
            String lap1 = 0+parts[1];
            String lap2 = 0+parts[2];
            String lap3 = 0+parts[3];
            driverList.add(new Driver(name, lap1, lap2, lap3));
        }
    }

    public void printSorted(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);

        driverList.sort(Comparator.comparing(Driver::getBestTime));

        driverList.forEach(pw::println);

        pw.flush();
    }
}

public class F1Test {

    public static void main(String[] args) throws IOException {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}
