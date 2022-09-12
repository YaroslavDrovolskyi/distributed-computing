package ua.edu.yarik.task_a;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskA {
    public static void main(String[] args){
        TaskManager manager = new TaskManager(50);
    }
}


class TaskManager{
    private BlockingQueue<Integer> freeTasks; // task is number of row in forest
    private final int forestSize;
    private Forest forest;
    private Thread[] threads = new Thread[4];
    private boolean bearFound = false;

    public TaskManager(int forestSize){
        this.forestSize = forestSize;
        this.forest = new Forest(forestSize);

        // fill rows numbers
        this.freeTasks = new LinkedBlockingQueue<Integer>(forestSize);
        for (int i = 0; i < forestSize; i++){
            freeTasks.add(i); // don't need blocking there
        }

        // create threads
        for (int i = 0; i < threads.length; i++){
            threads[i] = new Thread(new BeeThread(this, forest));
        }

        // launch threads
        for (Thread thread : threads){
            thread.start();
        }
    }

    public synchronized int getTask(){
        if (bearFound || freeTasks.isEmpty()){
            return -1; // this thread will interrupt itself
        }

        try {
            return freeTasks.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendResult(boolean searchingResult){
        bearFound = bearFound || searchingResult;
    }
}




