import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Canvas{
    String id;
    List<Integer> square_sides;
    public Canvas(String s){
        String[] parts = s.split(" ");
        id=parts[0];
        square_sides = Arrays.stream(parts)
                .skip(0)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
    public int getSize(){
        return square_sides.size();
    }
    public int getPerimeter(){
        return square_sides.stream()
                .mapToInt(side -> side)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("%s %d %d", id, getSize(), getPerimeter());
    }
}
class ShapesApplication{
    List<Canvas> canvasList;
    int totalSquares;
    public ShapesApplication(){
        canvasList=new ArrayList<>();
        totalSquares=0;
    }
    public int readCanvases(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        canvasList = br.lines().map(Canvas::new).collect(Collectors.toList());
        canvasList.forEach(canvas -> totalSquares += canvas.getSize());
        return totalSquares;
    }

    public void printLargestCanvasTo(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        int max=0;
        Canvas best = null;
        for(Canvas canvas:canvasList){
            if(canvas.getPerimeter()>max){
                max=canvas.getPerimeter();
                best = canvas;
            }
        }
        pw.println(best);
        pw.flush();
    }
}
public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}