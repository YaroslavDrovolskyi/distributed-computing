package ua.edu.yarik.task_b;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class HairdresserThread implements Runnable{
    private Semaphore myInvoker;
    private Queue<ClientThread> clientsQueue; // shared resource

    public HairdresserThread(Semaphore myInvoker, Queue<ClientThread> clientsQueue){
        this.myInvoker = myInvoker;
        this.clientsQueue = clientsQueue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            try {
                // this if statement only for clearer output
                if (myInvoker.availablePermits() > 0){
                    System.out.println("Hairdresser is go to next client");
                    myInvoker.acquire();
                }
                else{
                    System.out.println("Hairdresser is sleeping...");
                    myInvoker.acquire(); // sleep until client invoke
                    System.out.println("Hairdresser is invoked");
                }



            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            ClientThread client = null;
            synchronized (clientsQueue){
                client = clientsQueue.remove();
            }

            // client is sleep now
            Semaphore clientInvoker = client.getInvoker();

            System.out.println("Hairdresser is cutting hair on " + client.getName());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            clientInvoker.release(); // invoke client
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
