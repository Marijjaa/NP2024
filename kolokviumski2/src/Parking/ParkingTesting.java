package Parking;

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
    LocalDateTime enter;
    LocalDateTime exit;

    public ParkedCar(String registration, String spot, LocalDateTime enter) {
        this.registration = registration;
        this.spot = spot;
        this.enter = enter;
    }

    public String getRegistration() {
        return registration;
    }

    public String getSpot() {
        return spot;
    }

    public LocalDateTime getEnter() {
        return enter;
    }

    public LocalDateTime getExit() {
        return exit;
    }

    public void setExit(LocalDateTime exit) {
        this.exit = exit;
    }

    public long getDuration() {
        return DateUtil.durationBetween(enter, exit);
    }
}

class Parking {
    int capacity;
    Map<String, ParkedCar> activeCars;
    List<ParkedCar> history;

    public Parking(int capacity) {
        this.capacity = capacity;
        activeCars = new HashMap<>();
        history = new ArrayList<>();
    }

    public void update(String registration, String spot, LocalDateTime timestamp, boolean entrance) {
        if (entrance) {
            activeCars.putIfAbsent(registration, new ParkedCar(registration, spot, timestamp));
        } else {
            ParkedCar carExiting = activeCars.remove(registration);
            if (carExiting != null) {
                carExiting.setExit(timestamp);
                history.add(carExiting);
            }
        }
    }

    public void currentState() {
        System.out.printf("Capacity filled: %.2f%%%n", (double) activeCars.size() / capacity * 100.0);
        activeCars.values().stream()
                .sorted(Comparator.comparing(ParkedCar::getEnter).reversed())
                .forEach(parkedCar -> System.out.printf("Registration number: %s Spot: %s Start timestamp: %s%n", parkedCar.getRegistration(), parkedCar.getSpot(), parkedCar.getEnter()));
    }

    public void history() {
        history.stream()
                .sorted(Comparator.comparing(ParkedCar::getDuration).reversed())
                .forEach(parkedCar -> System.out.printf("Registration number: %s Spot: %s Start timestamp: %s End timestamp: %s Duration in minutes: %d%n", parkedCar.getRegistration(), parkedCar.getSpot(), parkedCar.getEnter(), parkedCar.getExit(), parkedCar.getDuration()));
    }

    public Map<String, Integer> carStatistics() {
        Map<String, Integer> registrationHistory = history.stream()
                .collect(Collectors.groupingBy(
                        ParkedCar::getRegistration,
                        Collectors.summingInt(car -> 1)
                ));

        activeCars.values().forEach(parkedCar -> {
            registrationHistory.put(
                    parkedCar.getRegistration(),
                    registrationHistory.getOrDefault(parkedCar.getRegistration(), 0) + 1
            );
        });

        return registrationHistory.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        TreeMap::new
                ));
    }

    public Map<String, Double> spotOccupancy(LocalDateTime start, LocalDateTime end) {
        Map<String, Double> spotOccupancy = new HashMap<>();
        List<ParkedCar> all = new ArrayList<>(history);
        all.addAll(activeCars.values());
        all.forEach(parkedCar -> {
                    String spot = parkedCar.getSpot();
                    LocalDateTime accStart = parkedCar.enter.isAfter(start) ? parkedCar.enter : start;
                    LocalDateTime accEnd = (parkedCar.exit == null || parkedCar.exit.isAfter(end)) ? end : parkedCar.exit;
                    if (!start.isAfter(end)) {
                        double duration = DateUtil.durationBetween(accStart, accEnd);
                        spotOccupancy.put(spot, spotOccupancy.getOrDefault(parkedCar.getSpot(), 0.0) + duration);
                    } else {
                        spotOccupancy.put(spot, 0.0);
                    }
                }
        );
        double totalD = DateUtil.durationBetween(start, end);
        return spotOccupancy.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        stringDoubleEntry -> stringDoubleEntry.getValue() / totalD * 100,
                        (a, b) -> a,
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
