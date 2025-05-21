
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class NonExistingItemException extends Exception{
    public NonExistingItemException(String message){
        super(message);
    }
}

abstract class Archive{
    int id;
    LocalDate dateArchived;

    public Archive(int id) {
        this.id = id;
    }

    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }

    public int getId() {
        return id;
    }
    public abstract boolean canOpen(LocalDate ld);
}
class LockedArchive extends Archive{
    LocalDate dateToOpen;
    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen=dateToOpen;
    }

    @Override
    public boolean canOpen(LocalDate ld) {
        return dateToOpen.isBefore(ld);
    }
    public LocalDate getDateToOpen(){
        return dateToOpen;
    }
}
class SpecialArchive extends Archive{
    int max;
    int opened;
    public SpecialArchive(int id, int max) {
        super(id);
        this.max = max;
        this.opened = 0;
    }

    public void setOpened() {
        this.opened+=1;
    }

    @Override
    public boolean canOpen(LocalDate ld) {
        return opened<max;
    }
    public int getMax(){
        return max;
    }

}
class ArchiveStore{
    List<Archive> archiveList;
    List<String> logs;
    public ArchiveStore() {
        archiveList=new ArrayList<>();
        logs = new ArrayList<>();
    }
    void archiveItem(Archive item, LocalDate date){
        archiveList.add(item);
        item.setDateArchived(date);
        logs.add(String.format("Item %d archived at %s", item.getId(), date));
    }
    void openItem(int id, LocalDate date) throws NonExistingItemException {
        Archive a = archiveList.stream()
                .filter(archive -> id == archive.getId())
                .findFirst().orElseThrow(() -> new NonExistingItemException(String.format("Item with id %d doesn't exist", id)));
        if (a.canOpen(date)){
            if (a instanceof SpecialArchive){
                ((SpecialArchive) a).setOpened();
            }
            logs.add(String.format("Item %d opened at %s", id, date));
        }else{
            if (a instanceof SpecialArchive){
                logs.add(String.format("Item %d cannot be opened more than %d times", id, ((SpecialArchive) a).getMax()));
            }else{
                logs.add(String.format("Item %d cannot be opened before %s", id, ((LockedArchive) a).getDateToOpen()));
            }
        }
    }

    public String getLog() {
        return String.join("\n", logs);
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}