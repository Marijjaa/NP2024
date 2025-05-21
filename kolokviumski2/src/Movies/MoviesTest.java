package Movies;

import java.util.*;
import java.util.stream.Collectors;

class Movie {
    String title;
    List<Integer> ratings;

    public Movie(String title, List<Integer> ratings) {
        this.title = title;
        this.ratings = ratings;
    }

    public double getAverageRating() {
        return ratings.stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings", title, getAverageRating(), ratings.size());
    }

    public String getTitle() {
        return title;
    }
}

class MoviesList {
    List<Movie> movies;

    public MoviesList() {
        this.movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings) {
        List<Integer> ratingsList = Arrays.stream(ratings)
                .boxed()
                .collect(Collectors.toList());
        movies.add(new Movie(title, ratingsList));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream()
                .sorted(Comparator.comparing(Movie::getAverageRating).reversed().thenComparing(Movie::getTitle))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Movie> top10ByRatingCoef() {
        int mostRatings = movies.stream()
                .mapToInt(movie -> movie.ratings.size())
                .max().orElse(0);
        return movies.stream()
                .sorted(Comparator.comparingDouble((Movie movie) -> movie.getAverageRating() * movie.ratings.size() / (double) mostRatings).reversed().thenComparing(Movie::getTitle))
                .limit(10)
                .collect(Collectors.toList());
    }
}

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

// vashiot kod ovde