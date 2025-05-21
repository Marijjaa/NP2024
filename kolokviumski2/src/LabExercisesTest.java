import java.util.*;
import java.util.stream.Collectors;

class Student {
    String index;
    List<Integer> points;

    public Student(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
    }

    public String getIndex() {
        return index;
    }

    public double getPoints() {
        return (double) points.stream()
                .mapToInt(Integer::intValue)
                .sum() / 10;
    }

    public int presence() {
        return points.size();
    }

    public int getYear() {
        return 20 - Integer.parseInt(index.substring(0, 2));
    }



    @Override
    public String toString() {
        return String.format("%s %s %.2f", index, presence() > 7 ? "YES" : "NO", getPoints());
    }
}


class LabExercises {
    List<Student> students;

    public LabExercises() {
        students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void printByAveragePoints(boolean asc, int i) {
        if (asc) {
            students.stream()
                    .sorted(Comparator.comparing(Student::getPoints).thenComparing(Student::getIndex))
                    .limit(i)
                    .forEach(System.out::println);
        } else
            students.stream()
                    .sorted(Comparator.comparing(Student::getPoints).thenComparing(Student::getIndex).reversed())
                    .limit(i)
                    .forEach(System.out::println);
    }

    public List<Student> failedStudents() {
        return students.stream()
                .filter(student -> student.presence() < 8)
                .sorted(Comparator.comparing(Student::getIndex))
                .collect(Collectors.toList());

    }

    public Map<Integer, Double> getStatisticsByYear() {
        return students.stream()
                .filter(student -> student.presence() > 7)
                .collect(Collectors.groupingBy(
                        Student::getYear,
                        TreeMap::new,
                        Collectors.averagingDouble(Student::getPoints)
                ));
    }
}

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}