package StreamingPlatform;

import java.util.*;
import java.util.stream.Collectors;

class User {
    String id;
    String name;
    Map<String, Integer> ratings;


    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.ratings = new HashMap<>();
    }

    public void addRating(String movieId, int rating) {
        this.ratings.put(movieId, rating);
    }

    public List<String> favMoviesList() {
        int max = this.ratings.values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(0);
        return ratings.entrySet().stream()
                .filter(entry -> entry.getValue() == max)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

class Movie {
    String id;
    String name;
    Map<String, Integer> ratings;

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
        this.ratings = new HashMap<>();
    }

    public double rating() {
        return ratings.values().stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0.0);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' + ", rating='" + rating() +
                '}';
    }
}

class StreamingPlatform {
    Map<String, Movie> movies;
    Map<String, User> users;

    public StreamingPlatform() {
        movies = new HashMap<>();
        users = new HashMap<>();
    }

    public void addMovie(String id, String name) {
        movies.put(id, new Movie(id, name));
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void addRating(String userId, String movieId, int rating) {
        users.get(userId).ratings.put(movieId, rating);
        movies.get(movieId).ratings.put(userId, rating);
    }

    public void topNMovies(int n) {
        movies.values().stream()
                .sorted(Comparator.comparing(Movie::rating).reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    public void favouriteMoviesForUsers(List<String> userIds) {
        for (String userId : userIds) {
            User u = users.get(userId);
            List<Movie> favMovies = u.favMoviesList().stream()
                    .map(movieId -> movies.get(movieId))
                    .collect(Collectors.toList());
            System.out.println(u);
            favMovies.stream()
                    .sorted(Comparator.comparing(Movie::rating).reversed())
                    .forEach(System.out::println);
        }
    }

    public void similarUsers(String userId) {
        Set<String> movieIds = movies.keySet();
        for (User u : users.values()){
            for (String mId : movieIds){
                u.ratings.putIfAbsent(mId, 0);
            }
        }
        User u = users.get(userId);
        users.values().stream()
                .filter(user -> !user.equals(u))
                .sorted((u1, u2) -> Double.compare(
                        CosineSimilarityCalculator.cosineSimilarity(u2.ratings, u.ratings),
                        CosineSimilarityCalculator.cosineSimilarity(u1.ratings, u.ratings)
                ))
                .forEach(user -> System.out.println(user + " " + CosineSimilarityCalculator.cosineSimilarity(user.ratings, u.ratings)));
    }
}

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
