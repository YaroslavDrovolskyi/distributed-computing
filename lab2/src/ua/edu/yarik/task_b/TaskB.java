package ua.edu.yarik.task_b;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class TaskB {
    public static void main(String[] args) {
        ItemsBuffer b1 = new ItemsBuffer(5);
        ItemsBuffer b2 = new ItemsBuffer(5);

        Thread producer = new Thread(new ProducerThread(10, b1));
        Thread mediator = new Thread(new MediatorThread(b1, b2));
        Thread consumer = new Thread(new ConsumerThread(b2));

        producer.start();
        mediator.start();
        consumer.start();

        try{
            producer.join();
            mediator.join();
            consumer.join();
        } catch(InterruptedException e){
            System.out.println(e);
        }

    }
}







