import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

class Episode {
    String episodeName;
    List<Double> ratings;

    public Episode(String episodeName, List<Double> ratings) {
        this.episodeName = episodeName;
        this.ratings = ratings;
    }

    public double getRating() {
        double avg = ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double minRatingFactor = Math.min(ratings.size() / 20.0, 1.0);
        return avg * minRatingFactor;
    }

    @Override
    public String toString() {
        return String.format("Episode: %s, Rating: %.2f", episodeName, getRating());
    }
}

abstract class PlatformItem{
    String name;
    List<String> genres;

    public PlatformItem(String name, List<String> genres) {
        this.name = name;
        this.genres = genres;
    }

    public List<String> getGenres() {
        return genres;
    }

    public abstract double getRating();

}

class Movie extends PlatformItem{
    List<Double> ratings;

    public Movie(String name, List<String> genres, List<Double> ratingList) {
        super(name, genres);
        this.ratings = ratingList;
    }

    @Override
    public double getRating() {
        double avg = ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double minRatingFactor = Math.min(ratings.size() / 20.0, 1.0);
        return avg * minRatingFactor;
    }
    @Override
    public String toString() {
        return String.format("Movie %s %.4f", name, getRating());
    }

}
class Series extends PlatformItem{
    List<Episode> episodes;

    public Series(String name, List<String> genres, List<Episode> episodes) {
        super(name, genres);
        this.episodes = episodes;
    }

    @Override
    public double getRating() {
        List<Episode> sorted = episodes.stream()
                .sorted(Comparator.comparing(Episode::getRating).reversed()) // reversed for descending order
                .collect(Collectors.toList());

        // Compute the average of the top 3 episodes
        double avg = 0.0;
        for (int i = 0; i < Math.min(3, sorted.size()); i++) { // To handle cases where there are less than 3 episodes
            avg += sorted.get(i).getRating();
        }
        return avg / Math.min(3, sorted.size());
    }

    @Override
    public String toString() {
        return String.format("TV Show %s %.4f (%d episodes)", name, getRating(), episodes.size());
    }
}

class StreamingPlatform{
    List<PlatformItem> items;

    public StreamingPlatform() {
        items=new ArrayList<>();
    }

    public void addItem(String data) {
        String[] parts = data.split(";");
        String name = parts[0];
        String genres = parts[1];
        List<String> genresList = Arrays.stream(genres.split(",")).collect(Collectors.toList());
        if(parts.length>3){
            List<Episode> episodes = new ArrayList<>();
            for(int i = 2; i< parts.length; i++){
                String episode = parts[i];
                String[] episodeParts = episode.split(" ");
                String episodeName = episodeParts[0];
                List<Double> ratingEpisode = Arrays.stream(episodeParts).skip(1).map(Double::parseDouble).collect(Collectors.toList());
                episodes.add(new Episode(episodeName, ratingEpisode));
            }
            items.add(new Series(name, genresList, episodes));
        }else{
            String[] ratings = parts[2].split(" ");
            List<Double> ratingList = Arrays.stream(ratings).map(Double::parseDouble).collect(Collectors.toList());
            items.add(new Movie(name, genresList, ratingList));
        }
    }

    public void listAllItems(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        List<PlatformItem> items1 = items.stream().sorted(Comparator.comparing(PlatformItem::getRating).reversed()).collect(Collectors.toList());
        for(PlatformItem item : items1){
            pw.println(item);
        }
        pw.flush();
    }

    public void listFromGenre(String data, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        List<PlatformItem> filteredItems = items.stream()
                .filter(item -> item.getGenres().contains(data))
                .sorted(Comparator.comparingDouble(PlatformItem::getRating).reversed())
                .collect(Collectors.toList());
        filteredItems.stream().forEach(pw::println);
        pw.flush();
    }
}

public class StreamingPlatformTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StreamingPlatform sp = new StreamingPlatform();
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String [] parts = line.split(" ");
            String method = parts[0];
            String data = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
            if (method.equals("addItem")){
                sp.addItem(data);
            }
            else if (method.equals("listAllItems")){
                sp.listAllItems(System.out);
            } else if (method.equals("listFromGenre")){
                System.out.println(data);
                sp.listFromGenre(data, System.out);
            }
        }

    }
}
