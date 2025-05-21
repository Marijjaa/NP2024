package Faculty;//package mk.ukim.finki.vtor_kolokvium;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}

class PassedCourse {
    String courseName;
    int grade;

    public PassedCourse(String courseName, int grade) {
        this.courseName = courseName;
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }

    public String getName() {
        return courseName;
    }
}

class Student {
    String id;
    int yearsOfStudies;
    //int term, String courseName, int grade
    Map<Integer, List<PassedCourse>> courseGrades;

    public Student(String id, int yearsOfStudies) {
        this.id = id;
        this.yearsOfStudies = yearsOfStudies;
        this.courseGrades = new HashMap<>();
        IntStream.rangeClosed(1, yearsOfStudies * 2).forEach(i -> courseGrades.putIfAbsent(i, new ArrayList<>()));
    }

    public int gradesSize() {
        return (int) courseGrades.values().stream().flatMap(Collection::stream)
                .mapToInt(PassedCourse::getGrade).count();
    }

    public double getAverage() {
        return courseGrades.values().stream().flatMap(Collection::stream)
                .mapToInt(PassedCourse::getGrade).average().orElse(5);
    }

    @Override
    public String toString() {
        return String.format("Student: %s Courses passed: %d Average grade: %.2f", id, gradesSize(), getAverage());
    }

    public void addGrade(int term, String courseName, int grade) {
        this.courseGrades.get(term).add(new PassedCourse(courseName, grade));
    }

    public String getId() {
        return id;
    }
}

class Faculty {
    Map<String, Student> students;
    List<Student> logs;

    Map<String, List<Integer>> courseGrades;


    public Faculty() {
        this.students = new HashMap<>();
        this.courseGrades = new HashMap<>();
        this.logs = new ArrayList<>();
    }

    void addStudent(String id, int yearsOfStudies) {
        this.students.putIfAbsent(id, new Student(id, yearsOfStudies));
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        if (!students.get(studentId).courseGrades.containsKey(term)) {
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s", term, studentId));
        }
        if (students.get(studentId).courseGrades.get(term).size() == 3) {
            throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d", studentId, term));
        }
        students.get(studentId).addGrade(term, courseName, grade);
        courseGrades.putIfAbsent(courseName, new ArrayList<>());
        courseGrades.get(courseName).add(grade);
        if (students.get(studentId).yearsOfStudies == 3 && students.get(studentId).gradesSize() == 18 ||
                students.get(studentId).yearsOfStudies == 4 && students.get(studentId).gradesSize() == 24) {
            logs.add(students.get(studentId));
            students.remove(studentId);
        }
    }

    String getFacultyLogs() {
        StringBuilder sb = new StringBuilder();
        logs.forEach(log -> sb.append(String.format("Student with ID %s graduated with average grade %.2f in %d years.", log.getId(), log.getAverage(), log.yearsOfStudies)).append("\n"));
        return sb.toString();
    }

    String getDetailedReportForStudent(String id) {
//        Student: [id]
//        Term 1:
//        Courses for term: [count]
//        Average grade for term: [average]
        StringBuilder sb = new StringBuilder();
        String sId = students.get(id).getId();
        sb.append("Student: ").append(sId).append("\n");
        students.get(id).courseGrades.forEach((key, value) -> {
            int term = key;
            int courses = value.size();
            double average = value.stream().mapToInt(PassedCourse::getGrade).average().orElse(5);
            sb.append(String.format("Term %d", term)).append(String.format("\nCourses: %d", courses)).append(String.format("\nAverage grade for term: %.2f%n", average));
        });
        sb.append(String.format("Average grade: %.2f", students.get(id).getAverage())).append("\n");
        sb.append("Courses attended: ");
        String courses = students.get(id).courseGrades.values().stream()
                .flatMap(Collection::stream)
                .map(passedCourse -> passedCourse.courseName)
                .sorted()
                .collect(Collectors.joining(","));
        sb.append(courses);
        return sb.toString();
    }

    void printFirstNStudents(int n) {
        students.values().stream()
                .sorted(Comparator.comparing(Student::gradesSize).thenComparing(Student::getAverage).thenComparing(Student::getId).reversed())
                .limit(n)
                .forEach(System.out::println);

    }

    void printCourses() {
        // [course_name] [count_of_students] [average_grade]
        courseGrades.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<Integer>> entry) -> entry.getValue().size())
                        .thenComparing((Map.Entry<String, List<Integer>> entry) -> entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(5))
                        .thenComparing(Map.Entry::getKey))
                .forEach(stringListEntry -> {
                    String course = stringListEntry.getKey();
                    double avg = stringListEntry.getValue().stream().mapToInt(Integer::intValue).average().orElse(5);
                    System.out.printf("%s %d %.2f%n", course, stringListEntry.getValue().size(), avg);
                });
    }
}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.printf(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase == 10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase == 11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i = 11; i < 15; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.printf(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
