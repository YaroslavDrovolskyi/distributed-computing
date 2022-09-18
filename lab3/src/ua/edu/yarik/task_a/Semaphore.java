package ua.edu.yarik.task_a;

public class Semaphore {
    private int permits; // count of threads allowed to enter sync section right now

    public Semaphore(int permits){
        this.permits = permits;
    }

    public synchronized void acquire(){
        while(permits == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        permits--;
        return;
    }

    public synchronized void release(){
        permits++;
        notify();
        return;
    }
}
