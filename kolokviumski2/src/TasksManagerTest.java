import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class DeadLineNotValidException extends Exception {
    public DeadLineNotValidException(LocalDateTime ld) {
        super(String.format("The deadline %s has already passed", ld));
    }
}

interface TaskComponent {
    Integer getPriority();

    LocalDateTime getDeadline();
}

class TaskBase implements TaskComponent {
    private String name;
    private String description;

    public TaskBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public Integer getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description +"'";
    }
}

abstract class TaskDecorator implements TaskComponent {
    TaskComponent taskComponent;

    public TaskDecorator(TaskComponent taskComponent) {
        this.taskComponent = taskComponent;
    }
}

class ExpiringTask extends TaskDecorator {
    private LocalDateTime deadLine;

    public ExpiringTask(TaskComponent taskComponent, LocalDateTime deadLine) throws DeadLineNotValidException {
        super(taskComponent);
        if (deadLine.isBefore(LocalDateTime.of(2020, 6, 2, 23, 59, 59)))
            throw new DeadLineNotValidException(deadLine);
        this.deadLine = deadLine;
    }

    @Override
    public Integer getPriority() {
        return taskComponent.getPriority();
    }

    @Override
    public LocalDateTime getDeadline() {
        return this.deadLine;
    }

    @Override
    public String toString() {
        return taskComponent +
                ", deadline=" + deadLine;
    }
}

class PriorityTask extends TaskDecorator {
    int priority;

    public PriorityTask(TaskComponent taskComponent, int priority) {
        super(taskComponent);
        this.priority = priority;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public LocalDateTime getDeadline() {
        return taskComponent.getDeadline();
    }

    @Override
    public String toString() {
        return taskComponent +
                ", priority=" + priority;

    }
}

class TaskManager {
    private List<TaskComponent> taskComponents;
    private Map<String, List<TaskComponent>> categoryToTasksMap;

    public TaskManager() {
        taskComponents = new ArrayList<>();
        categoryToTasksMap = new HashMap<>();
    }

    public void readTasks(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> inputs = br.lines().collect(Collectors.toList());
        for (String input : inputs) {
            try {
                String[] parts = input.split(",");
                TaskComponent taskComponent = new TaskBase(parts[1], parts[2]);
                if (parts.length == 4 || parts.length == 5) {
                    if (parts[3].length() > 10) {
                        taskComponent = new ExpiringTask(taskComponent, LocalDateTime.parse(parts[3]));
                    } else {
                        taskComponent = new PriorityTask(taskComponent, Integer.parseInt(parts[3]));
                    }
                }
                if (parts.length == 5) {
                    if (parts[4].length() > 10) {
                        taskComponent = new ExpiringTask(taskComponent, LocalDateTime.parse(parts[4]));
                    } else {
                        taskComponent = new PriorityTask(taskComponent, Integer.parseInt(parts[4]));
                    }
                }

                taskComponents.add(taskComponent);
                categoryToTasksMap.putIfAbsent(parts[0], new ArrayList<>());
                categoryToTasksMap.get(parts[0]).add(taskComponent);

            } catch (DeadLineNotValidException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        Comparator<TaskComponent> comparator;
        if (includePriority) {
            comparator = Comparator.comparing(TaskComponent::getPriority).reversed()
                    .thenComparing(taskComponent -> Duration.between(taskComponent.getDeadline(), LocalDateTime.now())).reversed();
        } else {
            comparator = Comparator.comparing((TaskComponent tc) -> Duration.between(tc.getDeadline(), LocalDateTime.now())).reversed();
        }
        if (includeCategory) {
            categoryToTasksMap.keySet().stream()
                    .forEach(key -> {
                        System.out.println(key.toUpperCase());
                        categoryToTasksMap.get(key).stream()
                                .sorted(comparator)
                                .forEach(taskComponent -> System.out.println(taskComponent + "}"));
                    });
        } else {
            taskComponents.stream()
                    .sorted(comparator)
                    .forEach(taskComponent -> System.out.println(taskComponent + "}"));
        }

    }
}


public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
