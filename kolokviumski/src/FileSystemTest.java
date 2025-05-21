import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class FileNameExistsException extends Exception{
    FileNameExistsException(String fileName, String folderName){
        super(String.format("There is already a file named %s in the folder %s", fileName, folderName));
    }
}

class IndentPrinter{
    public static String printIndentation(int level){
        return IntStream.range(0, level)
                .mapToObj(i -> "    ")
                .collect(Collectors.joining());
    }
}

interface IFile extends Comparable<IFile> {
    String getFileName();
    long getFileSize();
    String getFileInfo(int indent);
    void sortBySize();
    long findLargestFile ();
}
class File implements IFile{
    protected String fileName;
    protected long fileSize;
    public File(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public File(String fileName) {
        this.fileName = fileName;
    }
    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String getFileInfo(int indent) {
        return String.format("%sFile name: %10s File size: %10d%n", IndentPrinter.printIndentation(indent), getFileName(), getFileSize());
    }

    @Override
    public void sortBySize() {
        return;
    }

    @Override
    public long findLargestFile() {
        return this.getFileSize();
    }

    @Override
    public int compareTo(IFile o) {
        return Long.compare(this.getFileSize(), o.getFileSize());
    }
}
class Folder extends File implements IFile{
    List <IFile> files;
    private boolean ifNameExists(String fileName){
//        return files.stream().noneMatch(file -> file.getFileName().equals(fileName));
//        return files.stream().map(IFile::getFileName).filter(name -> name.equals(fileName)).count() == 0;
        return files.stream()
                .map(IFile::getFileName)
                .anyMatch(name -> name.equals(fileName));
    }
    public Folder(String fileName) {
        super(fileName);
        files = new ArrayList<>();
    }

    public void addFile(IFile file) throws FileNameExistsException {
        if (ifNameExists(file.getFileName())) {
            throw new FileNameExistsException(file.getFileName(), this.fileName);
        }
        files.add(file);
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public long getFileSize() {
        return (long) files.stream()
                .mapToDouble(IFile::getFileSize)
                .sum();
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sFolder name: %10s Folder size: %10d%n", IndentPrinter.printIndentation(indent), getFileName(), getFileSize()));
        files.stream().forEach(file -> sb.append(file.getFileInfo(indent + 1)));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        files = files.stream()
                .sorted()
                .collect(Collectors.toList());
        files.forEach(IFile::sortBySize);
    }

    @Override
    public long findLargestFile() {
        OptionalLong largest = files.stream().mapToLong(IFile::findLargestFile).max();
        if (largest.isPresent())
            return largest.getAsLong();
        else return 0L;
    }
}

class FileSystem{
    private Folder root;

    public FileSystem(){
        root = new Folder("root");
    }

    public void addFile(Folder folder) throws FileNameExistsException {
        root.addFile(folder);
    }

    public void sortBySize() {
        root.sortBySize();
    }

    public long findLargestFile() {
        return root.findLargestFile();
    }

    @Override
    public String toString() {
        return root.getFileInfo(0);
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());


    }
}