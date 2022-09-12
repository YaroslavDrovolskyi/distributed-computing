package ua.edu.yarik.task_b;

import java.util.Random;

public class ProducerThread implements Runnable{
    private ItemsBuffer outputBuffer;
    private final int itemsNumber;


    public ProducerThread(int itemsNumber, ItemsBuffer outputBuffer){
        this.itemsNumber = itemsNumber;
        this.outputBuffer = outputBuffer;
    }

    @Override
    public void run() {
        try{
            for (int i = 1; i <= itemsNumber; i++){
                Item newItem = new Item(i);
                outputBuffer.put(newItem);
                System.out.println("Producer passed " + newItem);
                Thread.sleep(1000);
            }
            System.out.println("Producer passed \'PoisonPill\' & finished work");
            outputBuffer.put(new Item(-1));
        }
        catch(InterruptedException e){
            return;
        }
    }
}