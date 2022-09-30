package ua.edu.yarik.task_a;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class NameFinder implements Callable<String> {
    private String threadName;
    private String filepath;
    private PhoneNumber phoneNumberToFind;
    private MyReentrantReadWriteLock lock;

    public NameFinder(String threadName, String filepath,
                      PhoneNumber phoneNumberToFind, MyReentrantReadWriteLock lock){
        this.threadName = threadName;
        this.filepath = filepath;
        this.phoneNumberToFind = phoneNumberToFind;
        this.lock = lock;
    }

    @Override
    public String call() throws Exception {
        String result = null;


        try{
            lock.readLock();
            System.out.println(threadName + " started reading");
            Scanner scanner = new Scanner(new File(filepath));

            int currentLineIndex = -1;
            while(scanner.hasNextLine()){
                currentLineIndex++;

                String inputLine = scanner.nextLine(); // read line without a '\n'
                String[] lineItems = FileRecordParser.parseInputLine(inputLine, currentLineIndex);
                if (phoneNumberToFind.equals(new PhoneNumber(lineItems[1]))){
                    result = lineItems[0];
                    break;
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

    public void setPhoneNumberToFind(PhoneNumber p){
        phoneNumberToFind = p;
    };
}
