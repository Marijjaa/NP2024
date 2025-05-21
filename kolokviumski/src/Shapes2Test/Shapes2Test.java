package Shapes2Test;
import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

class InvalidCanvasException extends Exception{
    public InvalidCanvasException(String message){
        super(message);
    }
}

abstract class Shape{
    double length;

    public Shape(double length) {
        this.length = length;
    }
    abstract public double getP();
}
class Square extends Shape{

    public Square(double length) {
        super(length);
    }

    @Override
    public double getP() {
        return length*length;
    }
}
class Circle extends Shape{

    public Circle(double length) {
        super(length);
    }

    @Override
    public double getP() {
        return length*length*Math.PI;
    }
}
class Canvas{
    List<Shape> shapes;
    String id;
    public Canvas(String str) {
        shapes = new ArrayList<>();
        String[] parts = str.split(" ");
        id = parts[0];
        for(int i=1; i< parts.length; i+=2){
            if(parts[i].equals("C")){
                shapes.add(new Circle(Double.parseDouble(parts[i+1])));
            }else {
                shapes.add(new Square(Double.parseDouble(parts[i+1])));
            }
        }
    }
    public int circleNum(){
        return (int) shapes.stream().filter(shape -> shape instanceof Circle).count();
    }

    public int squareNum(){
        return (int) shapes.stream().filter(shape -> shape instanceof Square).count();
    }
    public double maxArea(){
        return  shapes.stream()
                .mapToDouble(shape -> shape.getP())
                .max().orElse(0);
    }

    public double minArea(){
        return  shapes.stream()
                .mapToDouble(shape -> shape.getP())
                .min().orElse(0);
    }
    public double avgArea(){
        return  shapes.stream()
                .mapToDouble(shape -> shape.getP())
                .average().orElse(0);
    }
    public double sumArea(){
        return  shapes.stream()
                .mapToDouble(shape -> shape.getP())
                .sum();
    }
    @Override
    public String toString() {
        return String.format("%s %d, %d, %d, %.2f, %.2f, %.2f", id, shapes.size(), circleNum(), squareNum(), minArea(), maxArea(), avgArea());
    }

    public void fit(double maxArea) throws InvalidCanvasException {
        if(shapes.stream().filter(shape -> shape.getP()>maxArea).count()>0){
            throw new InvalidCanvasException(String.format("Canvas %s has a shape with area larger than %.2f.", id, maxArea));
        }
    }
}
class ShapesApplication{
    List<Canvas> list;
    double maxArea;
    public ShapesApplication(double maxArea) {
        list = new ArrayList<>();
        this.maxArea=maxArea;
    }

    public void readCanvases(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        list = br.lines().map(str -> {
                try {
                    Canvas c = new Canvas(str);
                    c.fit(maxArea);
                    return c;
                } catch (InvalidCanvasException e) {
                    System.out.println(e.getMessage());
                    return null;
                }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printCanvases(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        list.stream()
                .sorted(Comparator.comparingDouble(Canvas::sumArea).reversed())
                .forEach(pw::println);
        pw.flush();
    }
}


public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);

    }
}