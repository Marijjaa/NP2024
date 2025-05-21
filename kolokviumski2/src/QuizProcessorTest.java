import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class DifferentSizeException extends Exception{
    public DifferentSizeException(String message){
        super(message);
    }
}
class QuizAttempt{
    String id;
    List<String> studentsAnswers;
    List<String> correctAnswers;
    double points;
    public QuizAttempt(String id, List<String> studentsAnswers, List<String> correctAnswers) throws DifferentSizeException {
        if (studentsAnswers.size()!=correctAnswers.size()){
            throw new DifferentSizeException("A quiz must have same number of correct and selected answers");
        }
        this.id = id;
        this.studentsAnswers = studentsAnswers;
        this.correctAnswers = correctAnswers;
        this.points = 0;
    }
    public double getSummaryPoints(){
        IntStream.range(0, studentsAnswers.size()).forEach(i -> {
            if (studentsAnswers.get(i).equals(correctAnswers.get(i))) {
                points += 1;
            } else
                points -= 0.25;
        });
        return points;
    }

    public String getId() {
        return id;
    }
}
class QuizProcessor{
    static Map<String, QuizAttempt> studentsAttempts = new HashMap<>();


    public static Map<String, Double> processAnswers(InputStream in) {
        //ID; C1, C2, C3, C4, C5, … ,Cn; A1, A2, A3, A4, A5, …,An.
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> lines = br.lines().collect(Collectors.toList());
        for (String line:lines){
            String[] parts = line.split(";");
            String id = parts[0];
            List<String> studentAnswers= Arrays.stream(parts[1].split(", ")).collect(Collectors.toList());
            List<String> correctAnswers= Arrays.stream(parts[2].split(", ")).collect(Collectors.toList());
            try {
                studentsAttempts.putIfAbsent(id, new QuizAttempt(id, studentAnswers, correctAnswers));
            } catch (DifferentSizeException e) {
                System.out.println(e.getMessage());
            }
        }
        Map<String, Double> sortedMap = new TreeMap<>();
        studentsAttempts.forEach((id, attempt) -> sortedMap.put(id, attempt.getSummaryPoints()));
        return sortedMap;
//        return studentsAttempts.values().stream()
//                .collect(Collectors.toMap(
//                        QuizAttempt::getId,
//                        QuizAttempt::getSummaryPoints
//                )).entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (a, b) -> a,
//                        TreeMap::new
//                ));
    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}