package ua.edu.yarik.task_b;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GardenConsolePrinter implements Runnable{
    private Garden garden;
    private ReentrantReadWriteLock lock;

    public GardenConsolePrinter(Garden garden, ReentrantReadWriteLock lock){
        this.garden = garden;
        this.lock = lock;
    }

    @Override
    public void run(){
        Lock readLock = lock.readLock();
        while(!Thread.interrupted()){

            readLock.lock();
            System.out.println("Console printer started to print");

            for(int i = 0; i < garden.getSize(); i++){
                for (int j = 0; j < garden.getSize(); j++){
                    System.out.print(garden.getWaterState(i, j) + " ");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        readLock.unlock();
                        return;
                    }
                }
                System.out.println();
            }

            System.out.println("Console printer finished to print");
            readLock.unlock();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

