import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SetANdMapIntro
{
    public static void main(String[] args) {
//      ne cuva duplikati i gi sortira elementite(MORA COMPARABLE)
        Set<Integer> treeIntSet = new TreeSet<>(Comparator.reverseOrder());
        for (int i=0; i<10; i++){
            treeIntSet.add(i);
        }
        Set<Integer>treeIntSet2 = IntStream.range(0, 10)
                        .boxed()
                        .collect(Collectors.toCollection(TreeSet::new));
        System.out.println(treeIntSet);
        System.out.println(treeIntSet2);
//      comparable kluc, sortira po kluc, nema dupli klucevi,
        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("FinKI", "Finki");
        treeMap.put("FINKI", "FINKI");
        treeMap.put("NP", "Napredno Programiranje");
        treeMap.put("F", "Fakultet");
        treeMap.put("I", "Informaticki");
        System.out.println(treeMap);
//      o(1) dodavanje i contains, o(n) iteriranje
//      klucot na elementite mora da ima preoptovaren hash metod
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("FinKI", "Finki");
        hashMap.put("FINKI", "FINKI");
        hashMap.put("NP", "Napredno Programiranje");
        hashMap.put("F", "Fakultet");
        hashMap.put("I", "Informaticki");
        System.out.println(hashMap);
    }
}
