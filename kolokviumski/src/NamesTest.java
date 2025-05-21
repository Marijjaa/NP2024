import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class NamesTest {
    public static Map<String, Integer> createFromFile (String path) throws FileNotFoundException {
        Map<String, Integer> result = new HashMap<>();
        InputStream is = new FileInputStream(path);
        Scanner sc = new Scanner(is);
        while (sc.hasNext()){
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String name = parts[0];
            int freq = Integer.parseInt(parts[1]);
            result.put(name, freq);
        }
        return result;
    }
    public static void main(String[] args) throws FileNotFoundException {
        Map<String, Integer> boyNamesMap = createFromFile("src/boynames.txt");
        Map<String, Integer> girlNamesMap = createFromFile("src/girlnames.txt");
        //System.out.println(girlNamesMap);
        Set<String> allNames = new HashSet<>();
        allNames.addAll(boyNamesMap.keySet());
        allNames.addAll(girlNamesMap.keySet());
        Map<String, Integer> unisexNames = new HashMap<>();
        allNames.stream()
                .filter(name -> boyNamesMap.containsKey(name) && girlNamesMap.containsKey(name))
                .forEach(name -> unisexNames.put(name, boyNamesMap.get(name) + girlNamesMap.get(name)));
//                .forEach(name -> System.out.printf("%s: Male: %d Female: %d Total %d%n",
//                        name,
//                        boyNamesMap.get(name),
//                        girlNamesMap.get(name),
//                        boyNamesMap.get(name) + girlNamesMap.get(name)));
//        Set<Map.Entry<String, Integer>> entrySet = unisexNames.entrySet();
//        for (Map.Entry<String, Integer> entry : entrySet){
//            entry.
//        }
        unisexNames.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.printf("%s : %d%n", entry.getKey(), entry.getValue()));

    }
}
