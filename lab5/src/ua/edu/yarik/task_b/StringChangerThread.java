package ua.edu.yarik.task_b;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class StringChangerThread implements Runnable{
    int id;
    CyclicBarrier barrier;
    Manager manager;
    Random rand = new Random();

    public StringChangerThread(int id, CyclicBarrier barrier, Manager manager){
        this.id = id;
        this.barrier = barrier;
        this.manager = manager;
    }


    @Override
    public void run() {
        while(!manager.isDone()){
            String string = manager.getString(id);
            String result = "";

            for (int i = 0; i < string.length(); i++){
                char currentSymbol = string.charAt(i);
                char replacementSymbol = getReplacementSymbol(currentSymbol);

                // decide replace char or not
                if (rand.nextBoolean()){
                    result += replacementSymbol;
                }
                else{
                    result += currentSymbol;
                }
            }
            manager.setString(id, result);

            try {
                Thread.sleep(500);

                System.out.println(Thread.currentThread().getName() + " finished & go to barrier");
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(Thread.currentThread().getName() + " terminated, because no work");
    }


    private char getReplacementSymbol(char c) {
        switch (c) {
            case 'A':
                return 'C';
            case 'C':
                return 'A';
            case 'B':
                return 'D';
            case 'D':
                return 'B';
            default:
                throw new IllegalArgumentException("");
        }
    }
}

