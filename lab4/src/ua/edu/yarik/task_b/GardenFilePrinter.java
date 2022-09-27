package ua.edu.yarik.task_b;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GardenFilePrinter implements Runnable{
    private Garden garden;
    private ReentrantReadWriteLock lock;
    private String outputFilepath;

    public GardenFilePrinter(Garden garden, ReentrantReadWriteLock lock, String outputFilepath){
        this.garden = garden;
        this.lock = lock;
        this.outputFilepath = outputFilepath;
    }

    @Override
    public void run(){


        Lock readLock = lock.readLock();
        while(!Thread.interrupted()){
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilepath, true)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            readLock.lock();
            System.out.println("File printer started to print");

            out.println("========== Garden state at "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ==========\n");

            for(int i = 0; i < garden.getSize(); i++){
                for (int j = 0; j < garden.getSize(); j++){
                    out.print(garden.getWaterState(i, j) + " ");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        out.close();
                        readLock.unlock();
                        return;
                    }
                }
                out.println();
            }
            out.println("\n\n\n\n\n");
            out.close();

            System.out.println("File printer finished to print");
            readLock.unlock();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

