package ua.edu.yarik.task_b;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GardenerThread implements Runnable{
    Garden garden;
    private final int MIN_WATER_STATE = 5;
    ReentrantReadWriteLock lock;

    public GardenerThread(Garden garden, ReentrantReadWriteLock lock){
        this.garden = garden;
        this.lock = lock;
    }

    @Override
    public void run(){
        Lock writeLock = lock.writeLock();
        while(!Thread.interrupted()){

            writeLock.lock();
            System.out.println("Gardener started to water plants");

            for(int i = 0; i < garden.getSize(); i++){
                for (int j = 0; j < garden.getSize(); j++){
                    if (garden.getWaterState(i, j) < MIN_WATER_STATE){
                        try { // water plants
                            garden.setWaterState(i, j, 9);
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            writeLock.unlock();
                            return;
                        }
                    }


                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        writeLock.unlock();
                        return;
                    }
                }
            }

            System.out.println("Gardener finished to water plants");
            writeLock.unlock();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
