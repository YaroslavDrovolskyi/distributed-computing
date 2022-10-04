package ua.edu.yarik.task_a;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class MyBarrier {
    private int maxCount;
    private int count;

    private Queue<Semaphore> requests = new LinkedList<>();
    private ReentrantLock awaitLock = new ReentrantLock();
    public MyBarrier(int maxCount){
        this.maxCount = maxCount;
        this.count = maxCount;
    }


    public void await() throws InterruptedException {
        awaitLock.lock();
        count--;

        if (count == 0){
            System.out.println(Thread.currentThread().getName() + " is in await(), count == 0");
            openBarrier();
            awaitLock.unlock();
        }
        else{
            System.out.println(Thread.currentThread().getName() + " is in await(), count > 0");
            Semaphore s = new Semaphore(0);
            requests.add(s);
            awaitLock.unlock();
            s.acquire();
            // there barrier is opened
        }
    }


    private synchronized void openBarrier(){
        // run barrierAction there
        System.out.println("Barrier is opened");

        // notify all threads to continue
        for (int i = 0; i < maxCount-1; i++){
            requests.remove().release();
        }
        count = maxCount;
    }


    /*


    public synchronized void await() throws InterruptedException {
        count--;
        while(count > 0){
            wait();
        }
        // can run action there
        System.out.println(Thread.currentThread().getName() + ": Barrier is opened");
        notifyAll();
//        count = maxCount;
    }
     */
}
