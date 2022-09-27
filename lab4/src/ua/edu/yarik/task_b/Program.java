package ua.edu.yarik.task_b;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Program {
    public static void main(String[] args) {
        String filepath = "D:/Repositories/distributed-computing/lab4/src/resources/GardenState.txt";
        Garden garden = new Garden(5);
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        Thread gardener = new Thread(new GardenerThread(garden, lock));
        Thread nature = new Thread(new NatureThread(garden, lock));
        Thread consolePrinter = new Thread(new GardenConsolePrinter(garden, lock));
        Thread filePrinter = new Thread(new GardenFilePrinter(garden, lock, filepath));



        try {
            nature.start();
            Thread.sleep(1000);

            gardener.start();

            consolePrinter.start();

            filePrinter.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
