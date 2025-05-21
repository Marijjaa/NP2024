import java.util.*;
import java.util.stream.IntStream;

class SeatNotAllowedException extends Exception {
    public SeatNotAllowedException(String message) {
        super(message);
    }
}

class SeatTakenException extends Exception {
    public SeatTakenException(String message) {
        super(message);
    }
}

class Sector {
    String sectorName;
    int sectorSize;
    Map<Integer, Boolean> seatsOccupancy;
    boolean guest;
    boolean home;

    public Sector(String sectorName, int sectorSize) {
        this.sectorName = sectorName;
        this.sectorSize = sectorSize;
        home = false;
        guest = false;
        seatsOccupancy = new TreeMap<>();
        IntStream.range(1, sectorSize+1).forEach(i -> seatsOccupancy.putIfAbsent(i, false));//?
    }

    public boolean isSeatFree(int seat){
        if (seatsOccupancy.get(seat)) {
            return false;
        }
        //seatsOccupancy.put(seat, true);
        return true;
    }

    public boolean isGuest() {
        return guest;
    }

    public boolean isHome() {
        return home;
    }

    public void setGuest() {
        this.guest = true;
    }
    public void setHome(){
        this.home = true;
    }
    public int freeSeats(){
        return (int) seatsOccupancy.values().stream().filter(b -> !b).count();
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%",
                sectorName,
                freeSeats(),
                sectorSize,
                (double)(sectorSize-freeSeats())/sectorSize*100);
    }

    public String getCode() {
        return sectorName;
    }
}

class Stadium {
    String name;
    Map<String, Sector> sectors;

    public Stadium(String name) {
        this.name = name;
        sectors = new TreeMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sectorSizes) {
        IntStream.range(0, sectorSizes.length).forEach(i -> sectors.putIfAbsent(sectorNames[i], new Sector(sectorNames[i], sectorSizes[i])));
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        if (!sectors.get(sectorName).isSeatFree(seat)){
            throw new SeatTakenException("SeatTakenException");
        }

        if (type == 1 && sectors.get(sectorName).isGuest()){
            throw new SeatNotAllowedException("SeatNotAllowedException");
        }
        if (type == 2 && sectors.get(sectorName).isHome()){
            throw new SeatNotAllowedException("SeatNotAllowedException");
        }

        if (type == 1){
            sectors.get(sectorName).setHome();
        } else if (type == 2) {
            sectors.get(sectorName).setGuest();
        }
        sectors.get(sectorName).seatsOccupancy.put(seat, true);
    }

    public void showSectors() {
        sectors.values()
                .stream()
                .sorted(Comparator.comparing(Sector::freeSeats).reversed().thenComparing(Sector::getCode))
                .forEach(System.out::println);
    }
}

public class StadiumTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
