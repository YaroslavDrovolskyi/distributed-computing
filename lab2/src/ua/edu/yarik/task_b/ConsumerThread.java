package ua.edu.yarik.task_b;

import java.util.concurrent.BlockingQueue;

public class ConsumerThread implements Runnable{
    private ItemsBuffer inputBuffer;
    private int price = 0;

    public ConsumerThread(ItemsBuffer inputBuffer){
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            try {
                Item item = inputBuffer.take();

                if (!item.isValid()){
                    System.out.println("Consumer finished work, because poissonPill received");
                    System.out.println("Consumer received overall price: " + price);
                    return;
                }
                else{
                    price += item.getPrice();
                    System.out.println("Consumer received " + item);
                }
                Thread.sleep(900);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getPrice(){
        return this.price;
    }
}
