package PayrollSystem2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class BonusNotAllowedException extends Exception {
    public BonusNotAllowedException(String bonus) {
        super(String.format("Bonus of %s is not allowed", bonus));
    }
}

abstract class Employee {
    String id;
    String level;
    double bonus;
    double wageByLevel;

    public Employee(String id, String level, double wageByLevel) {
        this.id = id;
        this.level = level;
        this.wageByLevel = wageByLevel;
        this.bonus = 0;
    }

    public void setFixedBonus(double bonus) {
        this.bonus = bonus;
    }

    public void setPercentBonus(double bonus) {
        this.bonus = getSalary() * (bonus / 100.0);
    }

    abstract public double getSalary();

    public String getLevel() {
        return level;
    }

    public double getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", id, level, getSalary());
    }
}

class HourlyEmployee extends Employee {
    double hours;

    public HourlyEmployee(String id, String level, double hours, double wageByLevel) {
        super(id, level, wageByLevel);
        this.hours = hours;
    }

    @Override
    public double getSalary() {
        if (this.hours > 40)
            return wageByLevel * 40 + (this.hours - 40) * (wageByLevel * 1.5) + bonus;
        return wageByLevel * hours + bonus;
    }

    public double getOverTimeWage() {
        return this.hours > 40 ? (this.hours - 40) * (wageByLevel * 1.5) : 0.0;
    }

    @Override
    public String toString() {
        String s = super.toString() + String.format(" Regular hours: %.2f Overtime hours: %.2f", this.hours >= 40 ? 40 : this.hours, this.hours > 40 ? this.hours - 40 : 0);
        if (getBonus() > 0) s += String.format(" Bonus: %.2f", getBonus());
        return s;
    }
}

class FreelanceEmployee extends Employee {
    List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, List<Integer> ticketPoints, double wageByLevel) {
        super(id, level, wageByLevel);
        this.ticketPoints = ticketPoints;
    }

    @Override
    public double getSalary() {
        return ticketPoints.stream().mapToInt(Integer::intValue).sum() * wageByLevel + bonus;
    }

    public int getPoints() {
        return this.ticketPoints.stream().mapToInt(Integer::intValue).sum();
    }
    //F;ID;level;ticketPoints1;ticketPoints2;...;ticketPointsN; 10%
    public int getTicketsSize(){
        return this.ticketPoints.size();
    }
    @Override
    public String toString() {
        String s = super.toString() + String.format(" Tickets count: %d Tickets points: %d", ticketPoints.size(), ticketPoints.stream().mapToInt(i -> i).sum());
        if (getBonus() > 0) s += String.format(" Bonus: %.2f", getBonus());
        return s;
    }
}

class PayrollSystem {
    Map<String, Employee> employees;
    Map<String, Double> hourlyRateByLevel;
    Map<String, Double> ticketRateByLevel;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.employees = new HashMap<>();
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    public Employee createEmployee(String s) throws BonusNotAllowedException {
        String[] partsBySpace = s.split(" ");
        String[] partsByComma = partsBySpace[0].split(";");
        Employee emp = null;
        if (partsByComma[0].equals("H")) {
            double hours = Double.parseDouble(partsByComma[3]);
            emp = new HourlyEmployee(partsByComma[1], partsByComma[2], hours, hourlyRateByLevel.get(partsByComma[2]));
        } else {
            List<Integer> points = IntStream.range(3, partsByComma.length)
                    .map(i -> Integer.parseInt(partsByComma[i]))
                    .boxed()
                    .collect(Collectors.toList());
            emp = new FreelanceEmployee(partsByComma[1], partsByComma[2], points, ticketRateByLevel.get(partsByComma[2]));
        }
        if (partsBySpace.length > 1) {
            String bonusString = partsBySpace[1];
            if (bonusString.endsWith("%")) {
                // Percent bonus
                double bonus = Double.parseDouble(bonusString.replace("%", ""));
                if (bonus > 20)
                    throw new BonusNotAllowedException(bonusString);
                emp.setPercentBonus(bonus);
            } else {
                // Fixed bonus
                double bonus = Double.parseDouble(bonusString);
                if (bonus > 1000)
                    throw new BonusNotAllowedException(bonusString + "$");
                emp.setFixedBonus(bonus);
            }
        }
        employees.putIfAbsent(emp.id, emp);
        return emp;
    }


    public Map<String, Double> getOvertimeSalaryForLevels() {
        return employees.values().stream()
                .filter(employee -> employee instanceof HourlyEmployee)
                .map(e -> (HourlyEmployee) e)
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        Collectors.summingDouble(HourlyEmployee::getOverTimeWage)
                ));
    }

    public void printStatisticsForOvertimeSalary() {
        DoubleSummaryStatistics dss = employees.values().stream()
                .filter(employee -> employee instanceof HourlyEmployee)
                .map(e -> (HourlyEmployee) e)
                .mapToDouble(HourlyEmployee::getOverTimeWage)
                .summaryStatistics();
        System.out.println(String.format("Statistics for overtime salary: Min: %.2f Average: %.2f Max: %.2f Sum: %.2f",
                dss.getMin(), dss.getAverage(), dss.getMax(), dss.getSum()
        ));
    }

    public Map<String, Integer> ticketsDoneByLevel() {
        return employees.values().stream()
                .filter(employee -> employee instanceof FreelanceEmployee)
                .map(employee -> (FreelanceEmployee) employee)
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        Collectors.summingInt(FreelanceEmployee::getTicketsSize)
                ));
    }

    public Collection<Employee> getFirstNEmployeesByBonus(int n) {
        return employees.values().stream()
                .sorted(Comparator.comparing(Employee::getBonus).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}
