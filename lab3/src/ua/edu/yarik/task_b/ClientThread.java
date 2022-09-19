package ua.edu.yarik.task_b;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ClientThread implements Runnable{
    private Semaphore hairdresserInvoker;
    private Semaphore myInvoker;
    private Queue<ClientThread> clientsQueue; // shared resource
    private String name;

    public ClientThread(Semaphore hairdresserInvoker, Queue<ClientThread> clientsQueue, String name){
        this.hairdresserInvoker = hairdresserInvoker;
        this.clientsQueue = clientsQueue;
        this.name = name;
        this.myInvoker = new Semaphore(0);
    }


    @Override
    public void run() {
        while(!Thread.interrupted()){
            synchronized (clientsQueue){
                clientsQueue.add(this);
                System.out.println(getName() + " added to queue");
            }

            hairdresserInvoker.release(); // invoke hairdresser
            try {
                System.out.println(getName() + " is sleeping...");
                myInvoker.acquire(); // sleep until barber invoke
                // hairdresser cut clients' hair there
                System.out.println(getName() + " is ready & invoked");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // waiting for next time to go to the hairdresser
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }


    public String getName() {
        return name;
    }

    public Semaphore getInvoker(){
        return myInvoker;
    }
}
