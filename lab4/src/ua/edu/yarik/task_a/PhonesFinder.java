package ua.edu.yarik.task_a;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class PhonesFinder implements Callable<List<PhoneNumber>> {
    private String filepath;
    private String nameToFind;
    private MyReentrantReadWriteLock lock;

    public PhonesFinder(String filepath, String name, MyReentrantReadWriteLock lock){
        this.filepath = filepath;
        this.nameToFind = name;
        this.lock = lock;
    }

    @Override
    public List<PhoneNumber> call() throws Exception {
        List<PhoneNumber> result = new LinkedList<>();

        lock.readLock();
        Scanner scanner = new Scanner(new File(filepath));

        int currentLineIndex = -1;

        while(scanner.hasNextLine()){
            currentLineIndex++;

            String line = scanner.nextLine(); // read line without a '\n'
            String[] lineComponents = line.split(":");
            if (lineComponents.length != 2){
                throw new IllegalArgumentException(
                        "Illegal format of a line index " + currentLineIndex);
            }

            String name = lineComponents[0].toLowerCase(Locale.ENGLISH);
            String phone = lineComponents[1];
            if (nameToFind.equals(name)){
                result.add(new PhoneNumber(phone));
            }
        }
        scanner.close();
        lock.readUnlock();

        return result;
    }
}
