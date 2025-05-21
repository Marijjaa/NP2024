import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
class InvalidOperationException1 extends Exception {
    public InvalidOperationException1(String message) {
        super(message);
    }
}

abstract class Question{
    //TF;text;points;answer
    String text;
    int points;
    String answer;

    public Question(String text, int points, String answer) {
        this.text = text;
        this.points = points;
        this.answer = answer;
    }
    public double getPointsIfCorrect(){
        return points;
    }
    public abstract double getPointsIfFalse();

    public String getAnswer() {
        return answer;
    }

    public String getText() {
        return text;
    }


}
class TF extends Question{

    public TF(String text, int points, String answer) {
        super(text, points, answer);
    }

    @Override
    public double getPointsIfFalse() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("True/False Question: %s Points: %d Answer: %s",
                text,
                (int) getPointsIfCorrect(),
                getAnswer());
    }

}
class MC extends Question{

    public MC(String text, int points, String answer) {
        super(text, points, answer);
    }

    @Override
    public double getPointsIfFalse() {
        return -(points*0.2);
    }
    @Override
    public String toString() {
        return String.format("Multiple Choice Question: %s Points %d Answer: %s",
                text,
                (int) getPointsIfCorrect(),
                getAnswer());
    }
}
class Quiz{
    List<Question> questions;

    public Quiz() {
        questions=new ArrayList<>();
    }

    public void addQuestion(String s) throws InvalidOperationException1 {
        String[] parts = s.split(";");
        String text = parts[1];
        int points = Integer.parseInt(parts[2]);
        String answer = parts[3];
        if (parts[0].equals("TF")){
            questions.add(new TF(text, points, answer));
        }else{
            if (!answer.matches("[A-E]")) {
                throw new InvalidOperationException1(String.format("%s is not allowed option for this question", answer));
            }
            questions.add(new MC(text, points, answer));
        }

    }

    public void printQuiz(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        List<Question> sortedQuestions = questions.stream().sorted(Comparator.comparing(Question::getPointsIfCorrect).reversed()).collect(Collectors.toList());
        for (Question question:sortedQuestions){
            pw.println(question);
        }
        pw.flush();
    }

    public void answerQuiz(List<String> answers, OutputStream out) throws InvalidOperationException1 {
        if(answers.size()!=questions.size()){
            throw new InvalidOperationException1("Answers and questions must be of same length!");
        }
        PrintWriter pw = new PrintWriter(out);
        double totalPoints = 0;
        int questionNum = 1;
        for (Question question : questions){
            if (question.getAnswer().equals(answers.get(questionNum-1))){
                totalPoints+=question.getPointsIfCorrect();
                pw.printf("%d. %.2f\n", questionNum, question.getPointsIfCorrect());
            }else {
                totalPoints+=question.getPointsIfFalse();
                pw.printf("%d. %.2f\n", questionNum, question.getPointsIfFalse());
            }
            questionNum+=1;
        }
        pw.printf("Total points: %.2f\n", totalPoints);
        pw.flush();
    }
}
public class QuizTest {
    public static void main(String[] args) throws InvalidOperationException1 {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i=0;i<questions;i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        }

        List<String> answers = new ArrayList<>();

        int answersCount =  Integer.parseInt(sc.nextLine());

        for (int i=0;i<answersCount;i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase==1) {
            quiz.printQuiz(System.out);
        } else if (testCase==2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
