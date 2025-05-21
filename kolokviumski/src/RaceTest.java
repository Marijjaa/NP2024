import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Racer{
    int id;
    LocalTime startTime;
    LocalTime endTime;

    public Racer(String s) {
        String[] parts = s.split(" ");
        this.id = Integer.parseInt(parts[0]);
        this.startTime = LocalTime.parse(parts[1]);
        this.endTime = LocalTime.parse(parts[2]);
    }
    public Duration getTime(){
        return Duration.between(startTime, endTime);
    }

    @Override
    public String toString() {
        long seconds = getTime().getSeconds();
        long hours = seconds / 3600;
        seconds%=3600;
        long minutes = seconds / 60;
        seconds%=60;
        return String.format("%d %02d:%02d:%02d", id, hours, minutes, seconds);
    }
}
class TeamRace{

    public static void findBestTeam(InputStream in, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        Duration total=Duration.ZERO;
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<Racer> racers=br.lines().map(Racer::new).sorted(Comparator.comparing(Racer::getTime)).collect(Collectors.toList());
        for(int i=0; i<4; i++){
            pw.println(racers.get(i));
            total = total.plus(racers.get(i).getTime());
        }
        long seconds = total.getSeconds();
        long hours = seconds / 3600;
        seconds%=3600;
        long minutes = seconds / 60;
        seconds%=60;
        pw.printf("%02d:%02d:%02d", hours, minutes, seconds);
        pw.flush();
    }
}

public class RaceTest {
    public static void main(String[] args) {
        TeamRace.findBestTeam(System.in, System.out);
    }
}