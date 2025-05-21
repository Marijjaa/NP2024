import java.util.*;

class DuplicateNumberException extends Exception{
    DuplicateNumberException(String phone){
        super(String.format("Duplicate number :%s", phone));
    }
}

class Contact{
    String name;
    String phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
class PhoneBook{
    Set<String> allPhoneNumbers;
    Map<String, Set<Contact>> contactsBySubstring;
    public PhoneBook(){
        allPhoneNumbers = new HashSet<>();
        contactsBySubstring = new HashMap<>();
    }
    private List<String> substrings(String phone){
        List<String> result = new ArrayList<>();
        for (int len = 3; len <= phone.length(); len++){
            for (int i=0; i <=phone.length()-len; i++){
                result.add(phone.substring(i, i+len ));
            }
        }
        return result;
    }
    public void addContact(String name, String phone) throws DuplicateNumberException {
        if(allPhoneNumbers.contains(phone))
            throw new DuplicateNumberException(phone);
        allPhoneNumbers.add(phone);
        Contact c = new Contact(name, phone);
        List<String> subs = substrings(phone);
        for (String sub : subs){
            contactsBySubstring.putIfAbsent(sub, new TreeSet<>(Comparator.comparing(Contact::getName)
                                                        .thenComparing(Contact::getPhone)));
            contactsBySubstring.get(sub).add(c);
        }
    }
    public void contactsByNumber(String number){
        if(contactsBySubstring.containsKey(number)){
            contactsBySubstring.get(number).forEach(System.out::println);
        }
    }
}
public class PhoneBookTest {
    public static void main(String[] args) throws DuplicateNumberException {
        PhoneBook pb = new PhoneBook();
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        for(int i=0; i<n; i++){
            String[] parts = sc.nextLine().split("\\s+");
            try {
                pb.addContact(parts[0], parts[1]);
            }catch (DuplicateNumberException e){
                System.out.println(e.getMessage());
            }
        }
        pb.contactsByNumber("072");

    }
}
