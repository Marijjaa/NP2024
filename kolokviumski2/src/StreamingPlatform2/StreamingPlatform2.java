package StreamingPlatform2;

import java.util.*;
import java.util.stream.Collectors;

class CosineSimilarityCalculator {

    public static double cosineSimilarity(Map<String, Integer> c1, Map<String, Integer> c2) {
        return cosineSimilarity(c1.values(), c2.values());
    }

    public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
        int[] array1;
        int[] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < c1.size(); i++) {
            up += (array1[i] * array2[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down1 += (array1[i] * array1[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down2 += (array2[i] * array2[i]);
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}

class Movie {
    String id;
    String name;
    List<Integer> ratings;

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
        this.ratings = new ArrayList<>();
    }

    public double getAvgRating() {
        return ratings.stream().mapToInt(i -> i).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("Movie ID: %s Title: %s Rating: %.2f", id, name, getAvgRating());
    }

    public String getId() {
        return id;
    }
}

class User {
    String id;
    String username;
    Map<String, Integer> ratedMovies;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        this.ratedMovies = new HashMap<>();
    }

    public List<String> favMovies() {
        int max = ratedMovies.values().stream()
                .mapToInt(i -> i)
                .max()
                .orElse(0);
        return ratedMovies.entrySet().stream()
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue() == max)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

class StreamingPlatform {
    Map<String, Movie> movies;
    Map<String, User> users;

    public StreamingPlatform() {
        this.movies = new HashMap<>();
        this.users = new HashMap<>();
    }

    public void addMovie(String id, String name) {
        this.movies.putIfAbsent(id, new Movie(id, name));
    }

    public void addUser(String id, String name) {
        this.users.putIfAbsent(id, new User(id, name));
    }

    public void addRating(String userId, String movieId, int rating) {
        this.users.get(userId).ratedMovies.putIfAbsent(movieId, rating);
        this.movies.get(movieId).ratings.add(rating);
    }

    public void topNMovies(int n) {
        movies.values().stream()
                .sorted(Comparator.comparing(Movie::getAvgRating).reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    public void favouriteMoviesForUsers(List<String> users) {
        this.users.values().stream()
                .filter(user -> users.contains(user.id))
                .forEach(user -> {
                            System.out.printf("User ID: %s Name: %s%n", user.id, user.username);
                            List<String> favMovieIds = user.favMovies();
                            favMovieIds.stream()
                                    .map(string -> movies.get(string))
                                    .sorted(Comparator.comparing(Movie::getAvgRating).reversed().thenComparing(Movie::getId))
                                    .forEach(System.out::println);
                            System.out.println();
                        }
                );
    }

    public void similarUsers(String userId) {
        //DecimalFormat format = new DecimalFormat("#.################");
        movies.keySet()
                .forEach(movieId -> {
                    for (User user : users.values()) {
                        user.ratedMovies.putIfAbsent(movieId, 0);
                    }
                });
        User user = users.get(userId);
        users.values().stream()
                .sorted(Comparator.comparingDouble(
                        (User u1) -> CosineSimilarityCalculator.cosineSimilarity(user.ratedMovies, u1.ratedMovies)
                ).reversed())
                .filter(user1 -> user1!=user)
                .forEach(user1 ->
                        System.out.printf("User ID: %s Name: %s %s%n", user1.id, user1.username, CosineSimilarityCalculator.cosineSimilarity(user.ratedMovies, user1.ratedMovies))
                );
    }
}

public class StreamingPlatform2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StreamingPlatform sp = new StreamingPlatform();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            if (parts[0].equals("addMovie")) {
                String id = parts[1];
                String name = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
                sp.addMovie(id, name);
            } else if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                sp.addUser(id, name);
            } else if (parts[0].equals("addRating")) {
                //String userId, String movieId, int rating
                String userId = parts[1];
                String movieId = parts[2];
                int rating = Integer.parseInt(parts[3]);
                sp.addRating(userId, movieId, rating);
            } else if (parts[0].equals("topNMovies")) {
                int n = Integer.parseInt(parts[1]);
                System.out.println("TOP " + n + " MOVIES:");
                sp.topNMovies(n);
            } else if (parts[0].equals("favouriteMoviesForUsers")) {
                List<String> users = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                System.out.println("FAVOURITE MOVIES FOR USERS WITH IDS: " + users.stream().collect(Collectors.joining(", ")));
                sp.favouriteMoviesForUsers(users);
            } else if (parts[0].equals("similarUsers")) {
                String userId = parts[1];
                System.out.println("SIMILAR USERS TO USER WITH ID: " + userId);
                sp.similarUsers(userId);
            }
        }
    }
}
