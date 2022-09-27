package ua.edu.yarik.task_a;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.RandomAccess;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class FileRecordsRemover implements Callable<Integer> {
    private String threadName;
    private String filepath;
    private String tempFilePath
            = "D:/Repositories/distributed-computing/lab4/src/resources/tempPhonesList.txt";
    private MyReentrantReadWriteLock lock;
    private String nameToDelete;


    public FileRecordsRemover(String threadName, String filepath,
                              MyReentrantReadWriteLock lock, String nameToDelete){
        this.threadName = threadName;
        this.filepath = filepath;
        this.lock = lock;
        this.nameToDelete = nameToDelete;
    }

    @Override
    public Integer call() throws Exception {
        lock.writeLock();
        System.out.println(threadName + " started removing");

        File file = new File(filepath);
        File tempFile = new File(tempFilePath);

        Files.copy(file.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Scanner tempFileReader = new Scanner(tempFile);
        PrintWriter out = new PrintWriter(file);

        /* we will read temp file, and write into our file only records,
           that we don't need to delete
        */
        int deletedLinesCount = 0;
        int currentLineIndex = -1;
        while(tempFileReader.hasNextLine()){
            currentLineIndex++;
            String inputLine =  tempFileReader.nextLine();
            String[] lineItems = FileRecordParser.parseInputLine(inputLine, currentLineIndex);
            String name = lineItems[0];

            if (!name.equals(nameToDelete)){
                out.println(inputLine);
            }
            else{
                deletedLinesCount++;
            }
        }
        tempFileReader.close();
        out.close();

        tempFile.delete();
        Thread.sleep(3000);
        System.out.println(threadName + " finished removing");
        lock.writeUnlock();

        return deletedLinesCount;
    }

    public void setNameToDelete(String nameToDelete){
        this.nameToDelete = nameToDelete;
    }
}

