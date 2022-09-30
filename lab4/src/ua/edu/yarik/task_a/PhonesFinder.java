package ua.edu.yarik.task_a;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class PhonesFinder implements Callable<List<PhoneNumber>> {
    private String threadName;
    private String filepath;
    private String nameToFind;
    private MyReentrantReadWriteLock lock;

    public PhonesFinder(String threadName, String filepath, String nameToFind, MyReentrantReadWriteLock lock){
        this.threadName = threadName;
        this.filepath = filepath;
        this.nameToFind = nameToFind;
        this.lock = lock;
    }

    @Override
    public List<PhoneNumber> call() throws Exception {
        List<PhoneNumber> result = new LinkedList<>();

        try{
            lock.readLock();
            System.out.println(threadName + " started reading");
            Scanner scanner = new Scanner(new File(filepath));

            int currentLineIndex = -1;

            while(scanner.hasNextLine()){
                currentLineIndex++;

                String inputLine = scanner.nextLine(); // read line without a '\n'
                String[] lineItems = FileRecordParser.parseInputLine(inputLine, currentLineIndex);
                // [0]-is name, [1] is phone number
                if (nameToFind.equals(lineItems[0])){
                    result.add(new PhoneNumber(lineItems[1]));
                }
            }
            scanner.close();
            Thread.sleep(1000);
        }
        finally{
            System.out.println(threadName + " finished reading");
            lock.readUnlock();
        }

        return result;
    }

    public void setNameToFind(String nameToFind){
        this.nameToFind = nameToFind;
    }
}
