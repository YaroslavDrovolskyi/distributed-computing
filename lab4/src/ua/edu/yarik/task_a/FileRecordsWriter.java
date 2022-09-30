package ua.edu.yarik.task_a;

import java.io.*;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Callable;

public class FileRecordsWriter implements Callable<Integer> {
    private String threadName;
    private String filepath;
    private MyReentrantReadWriteLock lock;
    private List<String> linesToWrite;


    public FileRecordsWriter(String threadName, String filepath,
                             MyReentrantReadWriteLock lock, List<String> linesToWrite){
        this.threadName = threadName;
        this.filepath = filepath;
        this.lock = lock;
        this.linesToWrite = linesToWrite;
    }

    @Override
    public Integer call() throws Exception {
        int wroteLinesCount = 0;

        try{
            lock.writeLock();
            System.out.println(threadName + " started writing");

            FileWriter fw = new FileWriter(filepath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            for (String currentLine : linesToWrite){
                out.println(currentLine);
                wroteLinesCount++;
            }
            out.close();
            Thread.sleep(2000);

        }
        finally{
            System.out.println(threadName + " finished writing");
            lock.writeUnlock();
        }

        return wroteLinesCount;
    }

    public void setLinesToWrite(List<String> linesToWrite){
        this.linesToWrite = linesToWrite;
    }
}
