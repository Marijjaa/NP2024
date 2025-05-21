import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

class User {
    String id;
    String name;
    List<Float> orders;
    Map<String, Location> addresses;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.orders = new ArrayList<>();
        this.addresses = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation(String address) {
        return addresses.get(address);
    }

    public void addSpent(float cost) {
        this.orders.add(cost);
    }

    @Override
    public String toString() {
        double totalSpent = orders.stream().mapToDouble(spent -> (double) spent).sum();
        double averageSpent = orders.stream().mapToDouble(spent -> (double) spent).average().orElse(0.0);
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, orders.size(), totalSpent, averageSpent);
    }
}

class DeliveryPerson {
    String id;
    String name;
    Location location;
    List<Float> earnings;

    public DeliveryPerson(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.earnings = new ArrayList<>();
    }

    public void deliver(int distance) {
        int baseFee = 90;
        int additionalFee = (int) (distance / 10.0) * 10;
        earnings.add((float) (baseFee + additionalFee));
    }

    @Override
    public String toString() {
        double totalEarned = earnings.stream().mapToDouble(Float::doubleValue).sum();
        double averageEarned = earnings.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, earnings.size(), totalEarned, averageEarned);
    }

    public String getName() {
        return name;
    }

    public int getOrdersSize() {
        return earnings.size();
    }
}

class Restaurant {
    String id;
    String name;
    Location location;

    List<Float> orderCost;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        orderCost = new ArrayList<>();
    }

    public void addOrder(float cost) {
        orderCost.add(cost);
    }

    public double getAveragePrice() {
        return orderCost.stream().mapToDouble(Float::doubleValue).average().orElse(0);
    }

    @Override
    public String toString() {
        double totalEarned = orderCost.stream().mapToDouble(Float::doubleValue).sum();
        double averageEarned = orderCost.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f", id, name, orderCost.size(), totalEarned, averageEarned);
    }

    public String getName() {
        return name;
    }
}

class DeliveryApp {
    String appName;
    Map<String, User> users;
    Map<String, DeliveryPerson> deliveryPeople;
    Map<String, Restaurant> restaurants;

    public DeliveryApp(String appName) {
        this.appName = appName;
        this.users = new HashMap<>();
        this.restaurants = new HashMap<>();
        this.deliveryPeople = new HashMap<>();
    }

    public void addUser(String id, String name) {
        users.putIfAbsent(id, new User(id, name));
    }

    public void registerDeliveryPerson(String id, String name, Location location) {
        deliveryPeople.putIfAbsent(id, new DeliveryPerson(id, name, location));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.putIfAbsent(id, new Restaurant(id, name, location));
    }

    public void addAddress(String id, String addressName, Location location) {
        users.get(id).addresses.putIfAbsent(addressName, location);
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = users.get(userId);
        Restaurant restaurant = restaurants.get(restaurantId);
        if (user == null || restaurant == null) return;

        Location userLocation = user.getLocation(userAddressName);
        Location restaurantLocation = restaurant.location;

        DeliveryPerson assignedDeliveryPerson = deliveryPeople.values().stream()
                .sorted(Comparator.comparing((DeliveryPerson deliveryPerson) -> deliveryPerson.location.distance(restaurantLocation)).thenComparing(DeliveryPerson::getOrdersSize))
                .findFirst().orElse(null);

        if (assignedDeliveryPerson != null) {
            int restaurantUserDistance = restaurantLocation.distance(userLocation);

            assignedDeliveryPerson.location = userLocation;

            assignedDeliveryPerson.deliver(restaurantUserDistance);

            user.addSpent(cost);
            restaurant.addOrder(cost);
        }
    }

    public void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing((User user) -> user.orders.stream().mapToDouble(spent -> (double) spent).sum()).thenComparing(User::getName).reversed())
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparing(Restaurant::getAveragePrice).thenComparing(Restaurant::getName).reversed())
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream()
                .sorted(Comparator.comparing((DeliveryPerson dp) -> dp.earnings.stream().mapToDouble(spent -> (double) spent).sum()).thenComparing(DeliveryPerson::getName).reversed())
                .forEach(System.out::println);
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
