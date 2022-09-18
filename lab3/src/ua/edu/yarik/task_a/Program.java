package ua.edu.yarik.task_a;

public class Program {
    public static void main(String[] args) {
        Semaphore beeSemaphore = new Semaphore(1);
        Semaphore bearInvoker = new Semaphore(0);
        HoneyPot pot = new HoneyPot(10);

        // create threads
        Thread bear = new Thread(new BearThread(pot, bearInvoker));
//        bear.setDaemon(true);
        Thread[] bees = new Thread[4];


        for(int i = 0; i < bees.length; i++){
            bees[i] = new Thread(new BeeThread(pot, beeSemaphore, bearInvoker));
//            bees[i].setDaemon(true);
        }

        // start threads
        for (Thread bee : bees){
            bee.start();
        }
        bear.start();
    }
}
