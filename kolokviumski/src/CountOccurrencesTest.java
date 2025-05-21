import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountOccurrencesTest {
    public static int count(Collection<Collection<String>> c, String str){
        int counter = 0;
        for (Collection<String> collection : c){
            for (String element : collection){
                if (element.equalsIgnoreCase(str)){
                    counter++;
                }
            }
        }
        return counter;
    }
    public static int count2(Collection<Collection<String>> c, String str){
        return (int) c.stream()
                .flatMap(coll -> coll.stream())
                .filter(string -> string.equals(str))
                .count();
    }
    public static void main(String[] args) {

    }
}
