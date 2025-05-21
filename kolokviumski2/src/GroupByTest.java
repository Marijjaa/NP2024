import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

class Record {
    String studentId;

    String courseName;

    int grade;

    LocalDate timestamp;

    public Record(String studentId, String courseName, int grade, LocalDate timestamp) {
        this.studentId = studentId;
        this.courseName = courseName;
        this.grade = grade;
        this.timestamp = timestamp;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getGrade() {
        return grade;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String yearAndMonth() {
        return String.format("%04d-%02d", timestamp.getYear(), timestamp.getMonth().getValue());
    }
}

class Faculty {
    List<Record> students;

    public Faculty() {
        students = new ArrayList<>();
    }

    public void addRecord(String studentId, String courseName, int grade, LocalDate timestamp) {
        students.add(new Record(studentId, courseName, grade, timestamp));
    }

    public Map<String, Double> studentsAverageGrade() {
        return students.stream()
                .filter(record -> record.grade > 5)
                .collect(Collectors.groupingBy(
                        Record::getStudentId,
                        TreeMap::new,
                        Collectors.averagingDouble(Record::getGrade)
                ));
    }

    public Map<String, Double> coursesAverageGrade() {
        return students.stream()
                .filter(record -> record.grade > 5)
                .collect(Collectors.groupingBy(
                        Record::getCourseName,
                        TreeMap::new,
                        Collectors.averagingDouble(Record::getGrade)
                ));
    }

    public Map<String, Long> studentsPassedCoursesCount() {
        return students.stream()
                .filter(record -> record.grade > 5)
                .collect(Collectors.groupingBy(
                        Record::getStudentId,
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public Map<String, Long> coursesPassedStudentsCount() {
        return students.stream()
                .filter(record -> record.grade > 5)
                .collect(Collectors.groupingBy(
                        Record::getCourseName,
                        Collectors.mapping(Record::getStudentId, Collectors.toList())
                ))//TreeMap<String, List<String>> do tuka imam mapa so ime na kurs, set na polozeni studenti po kursot
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,//key-mapper ist
                        entry -> (long) entry.getValue().size(),//value-mapper
                        (a, b) -> a,//samo key e ist
                        TreeMap::new//supplier
                ));
    }

    public Map<String, List<String>> studentsPassedCourses() {
        return students.stream()
                .filter(record -> record.grade > 5)
                .collect(Collectors.groupingBy(
                        Record::getStudentId,
                        TreeMap::new,
                        Collectors.mapping(Record::getCourseName, Collectors.toList())
                ));
    }

    public Map<String, Map<String, Double>> averageGradePerExamSession() {
        return students.stream()
                .filter(record -> record.grade > 5)
                .collect(Collectors.groupingBy(
                        Record::yearAndMonth,
                        TreeMap::new,
                        Collectors.groupingBy(Record::getCourseName, TreeMap::new, Collectors.averagingDouble(Record::getGrade))
                ));
    }
}

public class GroupByTest {
    public static void main(String[] args) {
        Faculty faculty = new Faculty();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) {
                break;
            }
            String[] parts = input.split("\\s+");
            if (parts.length == 5 && parts[0].equalsIgnoreCase("addRecord")) {
                String studentId = parts[1];
                String courseName = parts[2];
                int grade = Integer.parseInt(parts[3]);
                LocalDate timestamp = LocalDate.parse(parts[4]);

                faculty.addRecord(studentId, courseName, grade, timestamp);
            }
        }

        while (true) {
            String method = scanner.nextLine().trim();
            switch (method) {
                case "studentsAverageGrade":
                    faculty.studentsAverageGrade().forEach((student, avgGrade) ->
                            System.out.printf("Student %s: %.2f%n", student, avgGrade));
                    break;

                case "coursesAverageGrade":
                    faculty.coursesAverageGrade().forEach((course, avgGrade) ->
                            System.out.printf("Course %s: %.2f%n", course, avgGrade));
                    break;

                case "studentsPassedCoursesCount":
                    faculty.studentsPassedCoursesCount().forEach((student, count) ->
                            System.out.printf("Student %s: %d courses%n", student, count));
                    break;

                case "coursesPassedStudentsCount":
                    faculty.coursesPassedStudentsCount().forEach((course, count) ->
                            System.out.printf("Course %s: %d students%n", course, count));
                    break;

                case "studentsPassedCourses":
                    faculty.studentsPassedCourses().forEach((student, courses) ->
                            System.out.printf("Student %s: %s%n", student, String.join(", ", courses)));
                    break;

                case "averageGradePerExamSession":
                    faculty.averageGradePerExamSession().forEach((session, courseGrades) -> {
                        System.out.printf("Session %s:%n", session);
                        courseGrades.forEach((course, avgGrade) ->
                                System.out.printf("  Course %s: %.2f%n", course, avgGrade));
                    });
                    break;

                case "END":
                    return;

                default:
                    System.out.println("Invalid method name. Please try again.");
            }
        }
    }
}