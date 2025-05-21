import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//class Line implements Comparable<Line>{
//    String line;
//    char c;
//
//    public Line(String line, char c) {
//        this.line = line;
//        this.c = c;
//    }
//    private int countOcc(){
//        int counter=0;
//        for(char c : line.toLowerCase().toCharArray()){
//            if (c==this.c){
//                counter++;
//            }
//        }
//        return counter;
//    }
//
//    @Override
//    public int compareTo(Line o) {
//        return Integer.compare(this.countOcc(),o.countOcc());
//    }
//
//    @Override
//    public String toString() {
//        return line;
//    }
//}
class LineProcessor{
    List<String> lines;

    public LineProcessor() {
        lines=new ArrayList<>();
    }
    private int countOcc(String line, char character){
//        int counter=0;
//        for(char c : line.toLowerCase().toCharArray()){
//            if (c==character){
//                counter++;
//            }
//        }
//        return counter;
        return  (int)line.toLowerCase()
                .chars().filter(c -> ((char)c==character))
                .count();
    }

    public void readLines(InputStream in, OutputStream out, char a) {

        PrintWriter pw = new PrintWriter(out);
//        Scanner sc = new Scanner(in);
//        while(sc.hasNextLine()){
//            String str = sc.nextLine();
//            Line line = new Line(str, a);
//            lines.add(line);
//        }
//        Line max = lines.get(0);
//        for (Line line:lines){
//            if(line.compareTo(max)>=0){
//                max=line;
//            }
//        }
//        sc.close();
//        pw.println(max);
//        pw.flush();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        lines=br.lines().collect(Collectors.toList());
//        lines.stream().max(new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return Integer.compare(countOcc(o1, a), countOcc(o2, a));
//            }
//        });
        Comparator<String> comparator = Comparator.comparing(str->countOcc(str,a));
        String max=lines.stream()
                .max(comparator.thenComparing(Comparator.naturalOrder()))
                .orElse("");
        pw.println(max);
        pw.flush();
    }
}
public class LineProcessorTest {
    public static void main(String[] args) {
        LineProcessor lineProcessor = new LineProcessor();

        lineProcessor.readLines(System.in, System.out, 'a');
    }
}