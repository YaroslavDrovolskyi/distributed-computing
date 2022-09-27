package ua.edu.yarik.task_b;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NatureThread implements Runnable{
    private Garden garden;
    private ReentrantReadWriteLock lock;

    public NatureThread(Garden garden, ReentrantReadWriteLock lock){
        this.garden = garden;
        this.lock = lock;
    }

    @Override
    public void run() {
        Random rand = new Random();
        Lock writeLock = lock.writeLock();
        while(!Thread.interrupted()){

            writeLock.lock();
            System.out.println("Nature started to edit garden");
            for (int i = 0; i < garden.getSize(); i++){
                for (int j = 0; j < garden.getSize(); j++){
                    try {
                        garden.setWaterState(i, j, rand.nextInt(10));
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        writeLock.unlock();
                        return;
                    }
                }
            }
            System.out.println("Nature finished to edit garden");
            writeLock.unlock();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
