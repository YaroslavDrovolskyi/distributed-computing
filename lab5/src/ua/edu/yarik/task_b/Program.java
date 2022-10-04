package ua.edu.yarik.task_b;

import java.util.concurrent.CyclicBarrier;

public class Program {
    public static void main(String[] args) {
        final int THREADS_NUMBER = 4;
        Manager manager = new Manager(THREADS_NUMBER);
        CyclicBarrier barrier = new CyclicBarrier(THREADS_NUMBER, manager);

        // create threads
        Thread[] threads = new Thread[THREADS_NUMBER];
        for (int i = 0; i < THREADS_NUMBER; i++){
            threads[i] = new Thread(new StringChangerThread(i, barrier, manager));
        }

        // start threads
        for (Thread thread : threads){
            thread.start();
        }
    }
}
