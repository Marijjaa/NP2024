import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class EmptyResultException extends Exception{
    public EmptyResultException(String s){
        super(s);
    }
}

class FilterAndSort{
    public static <T extends Comparable<T>> List<T> execute(List<T> items, Predicate<T> predicate) throws EmptyResultException {
        List<T> result = items.stream()
                .filter(predicate)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        if(result.isEmpty()){
            throw new EmptyResultException("No element met the criteria");
        }
        return result;
    }
}

class Student1 implements Comparable<Student1> {
    String id;
    List<Integer> grades;

    public Student1(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public List<Integer> getGrades(){
        return grades;
    }

    public double average() {
        return grades.stream().mapToDouble(i -> i).average().getAsDouble();
    }

    public int getYear() {
        return (24 - Integer.parseInt(id.substring(0, 2)));
    }

    public int totalCourses() {
        return Math.min(getYear() * 10, 40);
    }

    public double labAssistantPoints() {
        return average() * ((double) grades.size() / totalCourses()) * (0.8 + ((getYear()-1)*0.2)/3.0);
    }

    @Override
    public int compareTo(Student1 o) {
        return Comparator.comparing(Student1::labAssistantPoints)
                .thenComparing(Student1::average)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("Student %s (%d year) - %d/%d passed exam, average grade %.2f.\nLab assistant points: %.2f", id, getYear(), grades.size(), totalCourses(), average(), labAssistantPoints());
    }
}


public class FilterAndSortTest {
    public static void main(String[] args) throws EmptyResultException {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());
        int n = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // students
            int studentScenario = Integer.parseInt(sc.nextLine());
            List<Student1> students = new ArrayList<>();
            while (n > 0) {

                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                String id = parts[0];
                List<Integer> grades = Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                students.add(new Student1(id, grades));
                --n;
            }

            if (studentScenario == 1) {
                // filter and sort all students who have at least 8.0 points and are at least 3rd year student
                Predicate<Student1> predicate = new Predicate<Student1>() {
                    @Override
                    public boolean test(Student1 student1) {
                        return student1.average()>8.0 && student1.getYear()>=3;
                    }
                };
                try {
                    List<Student1> list = FilterAndSort.execute(students, predicate);
                    list.forEach(System.out::println);
                }catch (EmptyResultException e){
                    System.out.println(e.getMessage());
                }


            } else {
                //filter and sort all students who have passed at least 90% of their total courses with an average grade of at least 9.0
                Predicate<Student1> predicate = new Predicate<Student1>() {
                    @Override
                    public boolean test(Student1 student1) {
                        return student1.average()>=9.0 &&
                                student1.getGrades().size()>=student1.totalCourses()*0.9;
                    }
                };
                try {
                    List<Student1> list = FilterAndSort.execute(students, predicate);
                    list.forEach(System.out::println);
                }catch (EmptyResultException e){
                    System.out.println(e.getMessage());
                }
            }
        } else { //integers
            List<Integer> integers = new ArrayList<>();
            while (n > 0) {
                integers.add(Integer.parseInt(sc.nextLine()));
                --n;
            }

            //filter and sort all even numbers divisible with 15
            Predicate<Integer> predicate = new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer%15==0;
                }
            };
            try{
                List<Integer> list = FilterAndSort.execute(integers, predicate);
                list.forEach(System.out::println);
            }catch (EmptyResultException e){
                System.out.println(e.getMessage());
            }
        }

    }
}
