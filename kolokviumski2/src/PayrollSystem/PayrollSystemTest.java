package PayrollSystem;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class Employee {
    protected String id;
    protected String level;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    abstract public double getSalary(Map<String, Double> map);

    @Override
    public String toString() {
        //Employee ID: 157f3d Level: level10
        return String.format("Employee ID: %s Level: %s Salary: ", id, level);
    }
}

class HourlyEmployee extends Employee {
    double hoursSpendWorking;
    Map<String, Double> hourlyRateByLevel;

    public HourlyEmployee(String id, String level, double hoursSpendWorking, Map<String, Double> hourlyRateByLevel) {
        super(id, level);
        this.hoursSpendWorking = hoursSpendWorking;
        this.hourlyRateByLevel = hourlyRateByLevel;
    }

    @Override
    public double getSalary(Map<String, Double> map) {
        double hourWage = map.get(level);
        double regularHours = Math.min(40, hoursSpendWorking);
        double overtimeHours = Math.max(0, hoursSpendWorking - 40);
        return regularHours * hourWage + overtimeHours * hourWage * 1.5;
    }

    @Override
    public String toString() {
        double regularHours = Math.min(40, hoursSpendWorking);
        double overtimeHours = Math.max(0, hoursSpendWorking - 40);
        return super.toString() + String.format("%.2f Regular hours: %.2f Overtime hours: %.2f", getSalary(hourlyRateByLevel), regularHours, overtimeHours);
    }
}

class FreelanceEmployee extends Employee {
    List<Integer> pointsPerTicket;
    Map<String, Double> ticketRateByLevel;

    public FreelanceEmployee(String id, String level, List<Integer> pointsPerTicket, Map<String, Double> ticketRateByLevel) {
        super(id, level);
        this.pointsPerTicket = pointsPerTicket;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    @Override
    public double getSalary(Map<String, Double> map) {
        return pointsPerTicket.stream().mapToInt(Integer::intValue).sum() * map.get(level);
    }

    @Override
    public String toString() {
        return super.toString() + String.format("%.2f Tickets count: %d Tickets points: %d", getSalary(ticketRateByLevel), pointsPerTicket.size(), pointsPerTicket.stream().mapToInt(Integer::intValue).sum());
    }
}

class PayrollSystem {
    Map<String, Double> hourlyRateByLevel;
    Map<String, Double> ticketRateByLevel;
    List<Employee> employees;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        employees = new ArrayList<>();
    }

    public void readEmployees(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        br.lines().forEach(s -> {
            String[] parts = s.split(";");
            if (parts[0].equals("H")) {
                employees.add(new HourlyEmployee(parts[1], parts[2], Double.parseDouble(parts[3]), hourlyRateByLevel));
            } else {
                List<Integer> points = IntStream.range(3, parts.length)
                        .map(i -> Integer.parseInt(parts[i]))
                        .boxed()
                        .collect(Collectors.toCollection(ArrayList::new));
                employees.add(new FreelanceEmployee(parts[1], parts[2], points, ticketRateByLevel));
            }
        });
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(OutputStream out, Set<String> levels) {
        return employees.stream()
                .filter(employee -> levels.contains(employee.getLevel()))
                .collect(Collectors.groupingBy(Employee::getLevel, TreeMap::new,
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingDouble((Employee employee) ->
                                        employee.getSalary(employee instanceof HourlyEmployee ? hourlyRateByLevel : ticketRateByLevel))
                                .reversed()))
                ));
    }


}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}
