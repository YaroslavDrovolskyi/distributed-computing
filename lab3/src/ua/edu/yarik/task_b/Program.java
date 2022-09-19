package ua.edu.yarik.task_b;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Program {
    public static void main(String[] args) {
        Queue<ClientThread> clientsQueue = new LinkedList<ClientThread>();
        Semaphore hairdresserInvoker = new Semaphore(0);

        // create threads
        Thread hairdresser = new Thread(new HairdresserThread(hairdresserInvoker, clientsQueue));
        Thread[] clients = new Thread[5];
        for(int i = 0; i < clients.length; i++){
            clients[i] = new Thread(new ClientThread(hairdresserInvoker, clientsQueue,
                                "Client-"+String.valueOf(i)));
        }

        // start threads
        hairdresser.start();
        for(Thread c : clients){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            c.start();
        }
    }
}
