import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class Flight implements Comparable<Flight> {
    String from;
    String to;
    LocalTime time;
    int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = LocalTime.of(0, 0);
        setTime(time);
        this.duration = duration;
    }

    @Override
    public String toString() {
        int h = duration / 60;
        int m = duration % 60;
        String novden = "";
        if (time.getHour() * 60 + time.getMinute() + duration >= 60 * 24)
            novden = "+1d ";

        return String.format("%s-%s %s-%s %s%dh%02dm", from, to, time, time.plusMinutes(duration), novden, h, m);
    }

    public void setTime(int time) {
        this.time = this.time.plusMinutes(time);
    }

    @Override
    public int compareTo(Flight o) {
        return Comparator.comparing(Flight::getTo)
                .thenComparing(Flight::getTime)
                .thenComparing(Flight::getDuration)
                .compare(this, o);
    }

    public String getTo() {
        return to;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

}

class Airport {
    String name;
    String country;
    String code;
    int passengers;
    List<Flight> flights;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        this.flights = new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d", name, code, country, passengers);
    }
}

class Airports {

    Map<String, Airport> airports;

    public Airports() {
        this.airports = new HashMap<>();
    }

    public void addAirport(String name, String country, String code, int passengers) {
        airports.putIfAbsent(code, new Airport(name, country, code, passengers));
    }

    public void addFlights(String from, String to, int time, int duration) {
        Flight flight = new Flight(from, to, time, duration);
        airports.get(from).flights.add(flight);
        airports.get(to).flights.add(flight);
    }

    public void showFlightsFromAirport(String from) {
        AtomicInteger i = new AtomicInteger(1);
        System.out.println(airports.get(from));
        airports.get(from).flights.stream()
                .filter(flight -> flight.from.equals(from))
                .sorted()
                .forEach(flight -> System.out.println(i.getAndIncrement() + ". " + flight));
    }

    public void showDirectFlightsFromTo(String from, String to) {
        List<Flight> flights = airports.get(from).flights.stream()
                .filter(flight -> flight.from.equals(from) && flight.to.equals(to))
                .sorted()
                .collect(Collectors.toList());
        if (flights.isEmpty())
            System.out.println("No flights from " + from + " to " + to);
        else
            flights.forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to) {
        List<Flight> flights = airports.get(to).flights.stream()
                .filter(flight -> flight.to.equals(to))
                .sorted()
                .collect(Collectors.toList());
        if (flights.isEmpty())
            System.out.println("No flights to" + to);
        else
            flights.forEach(System.out::println);
    }

}

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}

// vashiot kod ovde

