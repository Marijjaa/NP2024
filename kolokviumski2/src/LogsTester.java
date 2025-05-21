import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


abstract class Log {
    String serviceName;
    String microserviceName;
    String message;
    long timestamp;

    abstract int severity();

    public Log(String serviceName, String microserviceName, String message, long timestamp) {
        this.serviceName = serviceName;
        this.microserviceName = microserviceName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public long getTimeStamp() {
        return timestamp;
    }
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("%s|%s [%s] %s T:%d", serviceName, microserviceName, getType(), message, timestamp);
    }
}

class WarnLog extends Log {

    @Override
    int severity() {
        return 1 + (message.contains("might cause error") ? 1 : 0);
    }

    public WarnLog(String serviceName, String microserviceName, String message, long timestamp) {
        super(serviceName, microserviceName, message, timestamp);
    }

    @Override
    public String getType() {
        return "WARN";
    }
}

class ErrorLog extends Log {

    @Override
    int severity() {
        return 3 + (message.contains("fatal") ? 2 : 0) + (message.contains("exception") ? 3 : 0);
    }

    public ErrorLog(String serviceName, String microserviceName, String message, long timestamp) {
        super(serviceName, microserviceName, message, timestamp);
    }

    @Override
    public String getType() {
        return "ERROR";
    }
}

class InfoLog extends Log {

    @Override
    int severity() {
        return 0;
    }

    public InfoLog(String serviceName, String microserviceName, String message, long timestamp) {
        super(serviceName, microserviceName, message, timestamp);
    }

    @Override
    public String getType() {
        return "INFO";
    }
}

class LogCollector {
    Set<Log> logs;
    Map<String, List<Log>> logsByService;

    public LogCollector() {
        this.logsByService = new HashMap<>();
    }

    public void addLog(String logStr) {
        String[] parts = logStr.split("\\s+");
        Log log;
        switch (parts[2]) {
            case "ERROR":
                log = new ErrorLog(parts[0], parts[1],
                        IntStream.range(3, parts.length).mapToObj(i -> parts[i]).collect(Collectors.joining(" ")),
                        Long.parseLong(parts[parts.length - 1]));
                break;
            case "WARN":
                log = new WarnLog(parts[0], parts[1],
                        IntStream.range(3, parts.length).mapToObj(i -> parts[i]).collect(Collectors.joining(" ")),
                        Long.parseLong(parts[parts.length - 1]));
                break;
            default:
                log = new InfoLog(parts[0], parts[1],
                        IntStream.range(3, parts.length).mapToObj(i -> parts[i]).collect(Collectors.joining(" ")),
                        Long.parseLong(parts[parts.length - 1]));
        }
        logsByService.putIfAbsent(log.serviceName, new ArrayList<>());
        logsByService.get(log.serviceName).add(log);
    }

    public void printServicesBySeverity() {
        logsByService.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<Log>> stringListEntry) -> stringListEntry.getValue().stream().mapToInt(Log::severity).average().orElse(0)).reversed())
                .map(stringListEntry -> {
                    String service = stringListEntry.getKey();
                    List<Log> logsList = stringListEntry.getValue();
                    int totalLogs = logsList.size();
                    int microServices = (int) logsList.stream().map(log -> log.microserviceName).distinct().count();
                    return String.format("Service name: %s Count of microservices: %d Total logs in service: %d Average severity for all logs: %.2f Average number of logs per microservice: %.2f"
                            , service, microServices, totalLogs, logsList.stream().mapToInt(Log::severity).average().orElse(0), totalLogs / (double) microServices);
                })
                .forEach(System.out::println);
    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
        return logsByService.get(service).stream()
                .filter(log -> microservice==null || log.microserviceName.equals(microservice))
                .collect(Collectors.groupingBy(Log::severity, Collectors.summingInt(log -> 1)));
    }

    public void displayLogs(String service, String microservice, String order) {
        Comparator<Log> comparator;
        switch(order){
            case "NEWEST_FIRST":
                comparator=Comparator.comparing(Log::getTimeStamp).reversed();
                break;
            case "OLDEST_FIRST":
                comparator=Comparator.comparing(Log::getTimeStamp);
                break;
            case "MOST_SEVERE_FIRST":
                comparator=Comparator.comparingInt(Log::severity).thenComparing(Log::getTimeStamp).reversed();
                break;
            default:
                comparator=Comparator.comparingInt(Log::severity).thenComparing(Log::getTimeStamp);
                break;
        }
        logsByService.get(service).stream()
                .filter(log -> microservice == null || log.microserviceName.equals(microservice))
                .sorted(comparator)
                .forEach(System.out::println);
    }
}

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order = null;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                System.out.println(line);

                collector.displayLogs(service, microservice, order);
            }
        }
    }
}