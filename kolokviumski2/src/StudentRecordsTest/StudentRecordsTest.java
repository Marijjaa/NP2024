package StudentRecordsTest;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * January 2016 Exam problem 1
 */
class Record {
    //ioqmx7 MT 10 8 10 8 10 7 6 9 9 9 6 8 6 6 9 9 8
    String id;
    String nasoka;
    List<Integer> grades;

    public Record(String id, String nasoka, List<Integer> grades) {
        this.id = id;
        this.nasoka = nasoka;
        this.grades = grades;
    }

    public String getNasoka() {
        return nasoka;
    }

    public double getAverage() {
        return grades.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", this.id, getAverage());
    }

    public String getId() {
        return id;
    }
}

class StudentRecords {
    Map<String, Record> students;

    public StudentRecords() {
        this.students = new HashMap<>();
    }

    public int readRecords(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        AtomicInteger i = new AtomicInteger();
        br.lines().forEach(string -> {
            i.getAndIncrement();
            String[] parts = string.split("\\s+");
            students.putIfAbsent(parts[0], new Record(parts[0], parts[1], IntStream.range(2, parts.length).map(c -> Integer.parseInt(parts[c])).boxed().collect(Collectors.toList())));
        });
        return i.get();
    }

    public void writeTable(OutputStream out) {
        Map<String, List<Record>> nasokaList = students.values().stream()
                .collect(Collectors.groupingBy(
                        Record::getNasoka
                ));
        nasokaList.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(stringListEntry -> {
                    System.out.println(stringListEntry.getKey());
                    stringListEntry.getValue().stream()
                            .sorted(Comparator.comparing(Record::getAverage).reversed().thenComparing(Record::getId))
                            .forEach(System.out::println);
                });
    }

    public void writeDistribution(OutputStream out) {
        PrintWriter writer = new PrintWriter(out);

        students.values().stream()
                .collect(Collectors.groupingBy(Record::getNasoka))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> -getCountOfGrade(entry.getValue())))
                .forEach(entry -> {
                    String nasoka = entry.getKey();
                    writer.println(nasoka);

                    Map<Integer, Long> gradeCounts = entry.getValue().stream()
                            .flatMap(record -> record.grades.stream())
                            .collect(Collectors.groupingBy(grade -> grade, Collectors.counting()));

                    IntStream.rangeClosed(6, 10).forEach(grade -> {
                        long count = gradeCounts.getOrDefault(grade, 0L);
                        writer.printf("%2d | %s(%d)%n", grade, "*".repeat((int) Math.ceil((double) count / 10)), count);
                    });
                });

        writer.flush();
    }

    private int getCountOfGrade(List<Record> records) {
        return records.stream()
                .flatMap(record -> record.grades.stream())
                .filter(grade -> grade == 10)
                .mapToInt(Integer::intValue)
                .sum();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here