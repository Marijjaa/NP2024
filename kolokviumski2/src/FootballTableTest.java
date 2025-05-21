import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Partial exam II 2016/2017
 */


class Game{
    String homeTeam;
    String awayTeam;
    int homeGoals;
    int awayGoals;

    public Game(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }
}
class FootballTable{
    Map<String, List<Game>> teamGames;

    public FootballTable() {
        this.teamGames=new HashMap<>();
    }
    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        Game game = new Game(homeTeam, awayTeam, homeGoals, awayGoals);
        teamGames.putIfAbsent(homeTeam, new ArrayList<>());
        teamGames.putIfAbsent(awayTeam, new ArrayList<>());
        teamGames.get(homeTeam).add(game);
        teamGames.get(awayTeam).add(game);
    }
    public void printTable() {
        StringBuilder sb = new StringBuilder();
        List<String> teams = new ArrayList<>(teamGames.keySet());
        teams.stream();
    }
}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

// Your code here

