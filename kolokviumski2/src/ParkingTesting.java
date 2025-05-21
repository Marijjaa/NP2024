import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class DateUtil {
    public static long durationBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes();
    }
}

class ParkedCar {
    String registration;
    String spot;
    LocalDateTime entryTime;
    LocalDateTime exitTime;


    public ParkedCar(String registration, String spot, LocalDateTime entryTime) {
        this.registration = registration;
        this.spot = spot;
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime timestamp) {
        this.exitTime = timestamp;
    }

    public long getDuration() {
        return exitTime != null ? DateUtil.durationBetween(entryTime, exitTime) : 0;
    }

    public String getRegistration() {
        return registration;
    }

    public String getSpot() {
        return spot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public String printEnd() {
        return String.format("End timestamp: %s Duration in minutes: %d", exitTime, getDuration());
    }

    @Override
    public String toString() {
        return String.format("Registration number: %s Spot: %s Start timestamp: %s %s", registration, spot, entryTime, exitTime != null ? printEnd() : "");
    }
}

class Parking {
    int capacity;
    Map<String, ParkedCar> parkedCars;
    List<ParkedCar> parkingHistory;

    public Parking(int capacity) {
        this.capacity = capacity;
        this.parkedCars = new HashMap<>();
        this.parkingHistory = new ArrayList<>();
    }

    public void update(String registration, String spot, LocalDateTime timestamp, boolean entrance) {
        if (entrance) {
            parkedCars.putIfAbsent(registration, new ParkedCar(registration, spot, timestamp));
        } else {
            ParkedCar car = parkedCars.remove(registration);
            if (car != null) {
                car.setExitTime(timestamp);
                parkingHistory.add(car);
            }
        }
    }

    public void currentState() {
        System.out.printf("Capacity filled: %.2f%%%n", (parkedCars.size() / (double) capacity) * 100.0);
        parkedCars.values().stream()
                .sorted(Comparator.comparing(ParkedCar::getEntryTime).reversed())
                .forEach(System.out::println);
    }

    public void history() {
        parkingHistory.stream()
                .sorted(Comparator.comparing(ParkedCar::getDuration).reversed())
                .forEach(System.out::println);
    }

    public Map<String, Integer> carStatistics() {
        Map<String, Integer> carStats = new HashMap<>();
        parkingHistory.forEach(parkedCar -> carStats.put(parkedCar.registration, carStats.getOrDefault(parkedCar.registration, 0) + 1));
        for (ParkedCar parkedCar : parkedCars.values())
            carStats.put(parkedCar.registration, carStats.getOrDefault(parkedCar.registration, 0) + 1);
        return carStats.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, Integer> entry) -> entry.getValue()).thenComparing(Map.Entry.comparingByKey()).reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }

    public Map<String, Double> spotOccupancy(LocalDateTime start, LocalDateTime end) {
        Map<String, Double> spotUsage = new HashMap<>();
        List<ParkedCar> allEntries = new ArrayList<>(parkingHistory);
        allEntries.addAll(parkedCars.values());

        for (ParkedCar parkedCar : allEntries) {
            LocalDateTime actualStart = parkedCar.getEntryTime().isAfter(start) ? parkedCar.getEntryTime() : start;
            LocalDateTime actualEnd = (parkedCar.getExitTime() == null || parkedCar.getExitTime().isAfter(end)) ? end : parkedCar.getExitTime();
            if (!actualStart.isAfter(actualEnd)) {
                double duration = DateUtil.durationBetween(actualStart, actualEnd);
                spotUsage.put(parkedCar.getSpot(), spotUsage.getOrDefault(parkedCar.getSpot(), 0.0) + duration);
            } else {
                spotUsage.put(parkedCar.getSpot(), 0.0);
            }
        }

        double totalDuration = DateUtil.durationBetween(start, end);
        for (Map.Entry<String, Double> entry : spotUsage.entrySet()) {
            spotUsage.put(entry.getKey(), (entry.getValue() / totalDuration) * 100);
        }
        return spotUsage.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, Double> entry) -> entry.getValue()).thenComparing(Map.Entry.comparingByKey()).reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));

    }
}

public class ParkingTesting {

    public static <K, V extends Comparable<V>> void printMapSortedByValue(Map<K, V> map) {
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.println(String.format("%s -> %s", entry.getKey().toString(), entry.getValue().toString())));

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int capacity = Integer.parseInt(sc.nextLine());

        Parking parking = new Parking(capacity);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equals("update")) {
                String registration = parts[1];
                String spot = parts[2];
                LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
                boolean entrance = Boolean.parseBoolean(parts[4]);
                parking.update(registration, spot, timestamp, entrance);
            } else if (parts[0].equals("currentState")) {
                System.out.println("PARKING CURRENT STATE");
                parking.currentState();
            } else if (parts[0].equals("history")) {
                System.out.println("PARKING HISTORY");
                parking.history();
            } else if (parts[0].equals("carStatistics")) {
                System.out.println("CAR STATISTICS");
                printMapSortedByValue(parking.carStatistics());
            } else if (parts[0].equals("spotOccupancy")) {
                LocalDateTime start = LocalDateTime.parse(parts[1]);
                LocalDateTime end = LocalDateTime.parse(parts[2]);
                printMapSortedByValue(parking.spotOccupancy(start, end));
            }
        }
    }
}
