package Courses;

//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;

class Student {
    String id;
    String name;
    int first_exam;
    int second_exam;
    int labPoints;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setFirst_exam(int first_exam) {
        this.first_exam = first_exam;
    }

    public void setSecond_exam(int second_exam) {
        this.second_exam = second_exam;
    }

    public void setLabPoints(int labPoints) {
        this.labPoints = labPoints;
    }

    public double getPoints() {
        return first_exam * 0.45 + second_exam * 0.45 + labPoints;
    }

    public int grade() {
        if (getPoints() < 50) {
            return 5;
        } else if (getPoints() < 60) {
            return 6;
        } else if (getPoints() < 70) {
            return 7;
        } else if (getPoints() < 80) {
            return 8;
        } else if (getPoints() < 90) {
            return 9;
        } else
            return 10;
    }


    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                id, name, first_exam, second_exam, labPoints, getPoints(), grade());
    }
}

class AdvancedProgrammingCourse {
    Map<String, Student> students;

    public AdvancedProgrammingCourse() {
        this.students = new HashMap<>();
    }

    public void addStudent(Student student) {
        students.putIfAbsent(student.id, student);
    }

    public void updateStudent(String idNumber, String activity, int points) throws Exception {
        if (!students.containsKey(idNumber)) {
            throw new IllegalArgumentException("");
        }

        switch (activity) {
            case "midterm1":
                this.students.get(idNumber).setFirst_exam(points);
                break;
            case "midterm2":
                this.students.get(idNumber).setSecond_exam(points);
                break;
            case "labs":
                this.students.get(idNumber).setLabPoints(points);
                break;
            default:
                throw new IllegalArgumentException("");
        }
    }

    public List<Student> getFirstNStudents(int n) {
        return students.values().stream()
                .sorted(Comparator.comparing(Student::getPoints).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> gradeDistribution = students.values().stream()
                .mapToInt(Student::grade)
                .sorted()
                .boxed()
                .collect(Collectors.groupingBy(
                        grade -> grade,
                        Collectors.summingInt(grade -> 1)
                ));
        gradeDistribution.putIfAbsent(5, 0);
        gradeDistribution.putIfAbsent(6, 0);
        gradeDistribution.putIfAbsent(7, 0);
        gradeDistribution.putIfAbsent(8, 0);
        gradeDistribution.putIfAbsent(9, 0);
        gradeDistribution.putIfAbsent(10, 0);
        return gradeDistribution;
    }

    public void printStatistics() {
        DoubleSummaryStatistics dss = students.values().stream().filter(student -> student.grade() > 5).mapToDouble(Student::getPoints).summaryStatistics();
        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f%n", dss.getCount(), dss.getMin(), dss.getAverage(), dss.getMax());
    }
}

public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) throws Exception {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                try {
                    advancedProgrammingCourse.updateStudent(idNumber, activity, points);
                }catch (Exception ignored){

                }
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}

