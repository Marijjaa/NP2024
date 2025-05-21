import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        // Vasiod kod ovde
        Map<String, Set<String>> charsMap = new LinkedHashMap<>();
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNext()){
            String word = sc.nextLine();
            String sortedWord = word.chars().mapToObj(c -> String.valueOf((char) c))
                    .sorted()
                    .collect(Collectors.joining());
            charsMap.putIfAbsent(sortedWord, new TreeSet<>());
            charsMap.get(sortedWord).add(word);
        }
        charsMap.values().stream()
                .filter(a -> a.size()>=5)
                .forEach(a -> {a.forEach(str -> System.out.print(str+" "));
                    System.out.println();
                });
    }
}
