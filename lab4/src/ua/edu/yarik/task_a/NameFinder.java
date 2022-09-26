package ua.edu.yarik.task_a;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class NameFinder implements Callable<String> {
    private String filepath;
    private PhoneNumber phoneNumberToSearch;
    private MyReentrantReadWriteLock lock;

    public NameFinder(String filepath, PhoneNumber phoneNumberToSearch, MyReentrantReadWriteLock lock){
        this.filepath = filepath;
        this.phoneNumberToSearch = phoneNumberToSearch;
        this.lock = lock;
    }

    @Override
    public String call() throws Exception {
        String result = null;

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

            String name = lineComponents[0];
            String phone = lineComponents[1].trim();
            if (phoneNumberToSearch.equals(new PhoneNumber(phone))){
                result = name;
                break;
            }
        }

        scanner.close();
        lock.readUnlock();

        return result;
    }
}
