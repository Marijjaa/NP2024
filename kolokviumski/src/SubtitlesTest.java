import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class SubtitleElement{
    private int id;
    private String start;
    private String end;
    private List<String> text;

    public SubtitleElement(int id, String start, String end, List<String> text) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public List<String> getText() {
        return text;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
class Subtitles{
    List<SubtitleElement> array;
    public Subtitles(){
        array = new ArrayList<>();
    }
    public int loadSubtitles(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        int counter = 0;
        while((line=br.readLine())!=null){
            if(line.isEmpty()){ //prazen red megju elementi
                continue;
            }
            int index=Integer.parseInt(line.trim());
            String timeLine = br.readLine();
            String[] startEnd = timeLine.split(" --> ");
            String start = startEnd[0];
            String end = startEnd[1];
            List<String> text = new ArrayList<>();
            while((line=br.readLine())!=null && !line.isEmpty()){  // prviot uslov za ako e posleden element vo cel input
                text.add(line);
            }
            SubtitleElement se = new SubtitleElement(index, start, end, text);
            array.add(se);
            counter++;
        }
        return counter;
    }

    public void shift(int ms) {
        for (SubtitleElement element : array) {
            element.setStart(shiftTime(element.getStart(), ms));
            element.setEnd(shiftTime(element.getEnd(), ms));
        }
    }

    private String shiftTime(String time, int ms) {
        String[] parts = time.split("[:,]");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int milliseconds = Integer.parseInt(parts[3]);

        int totalMilliseconds = (hours * 3600 + minutes * 60 + seconds) * 1000 + milliseconds + ms;

        if (totalMilliseconds < 0) {
            totalMilliseconds = 0;
        }

        hours = totalMilliseconds / 3600000;
        totalMilliseconds %= 3600000;
        minutes = totalMilliseconds / 60000;
        totalMilliseconds %= 60000;
        seconds = totalMilliseconds / 1000;
        milliseconds = totalMilliseconds % 1000;

        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds);
    }

    public void print() {
        for (SubtitleElement element : array) {
            System.out.println(element.getId());
            System.out.println(element.getStart() + " --> " + element.getEnd());
            for (String line : element.getText()) {
                System.out.println(line);
            }
            System.out.println();
        }
    }
}

public class SubtitlesTest {
    public static void main(String[] args) throws IOException {
        Subtitles subtitles = new Subtitles();
        try {
            int n = subtitles.loadSubtitles(System.in);
            System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
            subtitles.print();
            int shift = n * 37;
            shift = (shift % 2 == 1) ? -shift : shift;
            System.out.println(String.format("SHIFT FOR %d ms", shift));
            subtitles.shift(shift);
            System.out.println("+++++ SHIFTED SUBTITLES +++++");
            subtitles.print();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

// Вашиот код овде
