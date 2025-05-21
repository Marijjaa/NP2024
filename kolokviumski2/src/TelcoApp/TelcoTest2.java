package TelcoApp;//package mk.ukim.finki.midterm;


import java.util.*;
import java.util.stream.Collectors;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}

abstract class State {
    Call call;

    public State(Call call) {
        this.call = call;
    }

    abstract void answer(long timestamp) throws Exception;

    abstract void end(long timestamp) throws Exception;

    abstract void hold(long timestamp) throws Exception;

    abstract void resume(long timestamp) throws Exception;
}

class RingingState extends State {

    public RingingState(Call call) {
        super(call);
    }

    @Override
    void answer(long timestamp) {
        this.call.start = timestamp;
        this.call.state = new InProgressState(this.call);
    }

    @Override
    void end(long timestamp) {
        this.call.end = timestamp;
        this.call.state = new IdleState(this.call);
    }

    @Override
    void hold(long timestamp) {

    }

    @Override
    void resume(long timestamp) {

    }
}

class InProgressState extends State {

    public InProgressState(Call call) {
        super(call);
    }

    @Override
    void answer(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }

    @Override
    void end(long timestamp) {
        this.call.end = timestamp;
        this.call.state = new IdleState(this.call);
    }

    @Override
    void hold(long timestamp) {
        this.call.holdStarted = timestamp;
        this.call.state = new PausedState(this.call);
    }

    @Override
    void resume(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }
}

class PausedState extends State {
    public PausedState(Call call) {
        super(call);
    }

    @Override
    void answer(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }

    @Override
    void end(long timestamp) {
        this.call.end = timestamp;
        this.call.timeInHold += (timestamp-this.call.holdStarted);
        this.call.holdStarted = null;
        this.call.state = new IdleState(this.call);
    }

    @Override
    void hold(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }

    @Override
    void resume(long timestamp) {
        this.call.timeInHold += (timestamp - this.call.holdStarted);
        this.call.holdStarted = null;
        this.call.state = new InProgressState(this.call);
    }
}

class IdleState extends State {

    public IdleState(Call call) {
        super(call);
    }

    @Override
    void answer(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }

    @Override
    void end(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }

    @Override
    void hold(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }

    @Override
    void resume(long timestamp) throws Exception {
        throw new Exception("Not allowed action");
    }
}

class Call {
    String uuid;
    String dialer;
    String receiver;
    Long initialized;
    Long start;
    Long end;
    Long holdStarted;
    long timeInHold;
    State state;

    public Call(String uuid, String dialer, String receiver, long initialized) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.initialized = initialized;
        state = new RingingState(this);
    }

    public void update(String action, long timestamp) throws Exception {
        if (action.equals("ANSWER")) {
            state.answer(timestamp);
        } else if (action.equals("HOLD")) {
            state.hold(timestamp);
        } else if (action.equals("RESUME")) {
            state.resume(timestamp);
        } else {
            state.end(timestamp);
        }
    }

    public long totalDuration() {
        return start == null ? 0 : end - start - timeInHold;
    }

    public String printReport(String phoneNumber) {
        StringBuilder sb = new StringBuilder();
        //sb.append(uuid);
        if (dialer.equals(phoneNumber))
            sb.append("D").append(" ").append(receiver);
        else
            sb.append("R").append(" ").append(dialer);
        sb.append(" ").append(start != null ? start : end).append(" ").append(start != null ? end : "MISSED CALL").append(" ");
        sb.append(DurationConverter.convert(totalDuration()));
        return sb.toString();
    }

    public Long getStart() {
        return start == null ? initialized : start;
    }
}


class TelcoApp {
    Map<String, Call> calls = new TreeMap<>();
    Map<String, List<Call>> callsByPhoneNumber = new TreeMap<>();

    public void addCall(String uuid, String dialer, String receiver, long initialized) {
        Call call = new Call(uuid, dialer, receiver, initialized);
        calls.put(uuid, call);

        callsByPhoneNumber.putIfAbsent(dialer, new ArrayList<>());
        callsByPhoneNumber.get(dialer).add(call);

        callsByPhoneNumber.putIfAbsent(receiver, new ArrayList<>());
        callsByPhoneNumber.get(receiver).add(call);
    }

    public void updateCall(String uuid, long timestamp, String action) throws Exception {
        calls.get(uuid).update(action, timestamp);
    }

    public void printChronologicalReport(String phoneNumber) {
        callsByPhoneNumber.get(phoneNumber).stream()
                .sorted(Comparator.comparing(Call::getStart))
                .forEach(call -> System.out.println(call.printReport(phoneNumber)));
    }

    public void printReportByDuration(String phoneNumber) {
        callsByPhoneNumber.get(phoneNumber).stream()
                .sorted(Comparator.comparing(Call::totalDuration).thenComparing(Call::getStart).reversed())
                .forEach(call -> System.out.println(call.printReport(phoneNumber)));
    }

    public void printCallsDuration() {
        Map<String, Long> callPairsMap = calls.values().stream()
                .collect(Collectors.groupingBy(
                        call -> String.format("%s <-> %s", call.dialer, call.receiver),
                        TreeMap::new,
                        Collectors.summingLong(Call::totalDuration)
                ));
        callPairsMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(stringLongEntry -> System.out.printf("%s : %s%n", stringLongEntry.getKey(), DurationConverter.convert(stringLongEntry.getValue())));
    }
}

public class TelcoTest2 {
    public static void main(String[] args) throws Exception {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }

    }
}
