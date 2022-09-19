package ua.edu.yarik.task_a;

public class BearThread implements Runnable{
    private Semaphore myInvoker;
    private Semaphore beesInvoker;
    private HoneyPot honeyPot;

    public BearThread(HoneyPot honeyPot, Semaphore myInvoker, Semaphore beesInvoker){
        this.honeyPot = honeyPot;
        this.myInvoker = myInvoker;
        this.beesInvoker = beesInvoker;
    }


    @Override
    public void run() {
        while(!Thread.interrupted()){
            myInvoker.acquire();
            synchronized (honeyPot){
                if(honeyPot.isFull()){
                    eatHoney(); // sleep 5s
                }
                else{
                    System.out.println("Bear tried eat honey, but pot isn't full");
                }
            }
            beesInvoker.release(); // invoke bees
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
