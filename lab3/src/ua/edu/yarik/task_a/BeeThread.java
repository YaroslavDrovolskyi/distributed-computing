package ua.edu.yarik.task_a;

public class BeeThread implements Runnable{
    private Semaphore semaphore;
    private Semaphore bearInvoker;
    private HoneyPot honeyPot;
    private boolean haveHoney = false;

    public BeeThread(HoneyPot honeyPot, Semaphore semaphore, Semaphore bearInvoker){
        this.honeyPot = honeyPot;
        this.semaphore = semaphore;
        this.bearInvoker = bearInvoker;
    }


    @Override
    public void run() {
        while(!Thread.interrupted()){
            if (!haveHoney){
                obtainHoney(); // sleep 1s
            }

            semaphore.acquire();
            synchronized (honeyPot){
                if (!honeyPot.isFull()){
                    putHoney(); // sleep 1.5s
                    if (honeyPot.isFull()){
                        bearInvoker.release(); // it will invoke bear
                        System.out.println(Thread.currentThread().getName() + " invoked Bear");
                    }
                }
            }
            semaphore.release();

        }
    }

    private void obtainHoney(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
//            System.out.println(Thread.currentThread().getName() + " interrupted while obtaining honey");
        }
        this.haveHoney = true;
    }

    private void putHoney(){
        honeyPot.putHoney();
        this.haveHoney = false;
        System.out.println(Thread.currentThread().getName() + " put honey");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
//            System.out.println(Thread.currentThread().getName() + " interrupted while putting honey");
        }
    }
}
