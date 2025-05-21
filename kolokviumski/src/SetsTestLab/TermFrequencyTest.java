package SetsTestLab;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class TermFrequency{
    private Map<String, Integer> wordCount;
    private Set<String> stop;

    public TermFrequency(InputStream in, String[] stop) {
        wordCount = new HashMap<>();
        this.stop = new HashSet<>(Arrays.asList(stop));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        br.lines()
                .flatMap(line -> Arrays.stream(line.toLowerCase()
                        .replaceAll("\\p{Punct}", "")
                        .split("\\s+")))
                .filter(word -> !word.isEmpty() && !this.stop.contains(word))
                .forEach(word -> wordCount.put(word, wordCount.getOrDefault(word, 0)+1));
    }

    public int countTotal() {
        return wordCount.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int countDistinct() {
        return wordCount.size();
    }

    public List<String> mostOften(int i) {
        return wordCount.entrySet().stream()
                .sorted(Comparator.comparingInt((Entry<String, Integer> e) -> e.getValue()).reversed()
                        .thenComparing(Entry::getKey))
                .limit(i)
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }
}

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
// vasiot kod ovde

