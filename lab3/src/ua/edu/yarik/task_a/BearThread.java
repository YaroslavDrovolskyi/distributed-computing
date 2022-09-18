package ua.edu.yarik.task_a;

public class BearThread implements Runnable{
    private Semaphore semaphore;
    private HoneyPot honeyPot;

    public BearThread(HoneyPot honeyPot, Semaphore semaphore){
        this.honeyPot = honeyPot;
        this.semaphore = semaphore;
    }


    @Override
    public void run() {
        while(!Thread.interrupted()){
            semaphore.acquire();
            synchronized (honeyPot){
                if(honeyPot.isFull()){
                    eatHoney(); // sleep 5s
                }
                else{
                    System.out.println("Bear tried eat honey, but pot isn't full");
                }
            }
//            semaphore.release();
        }
    }

    private void eatHoney(){
        honeyPot.getAllHoney();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
//            System.out.println("Bear interrupted while eating honey");
        }

        System.out.println("Bear ate all honey");
    }
}
