package ua.edu.yarik.task_b;

import java.util.concurrent.BlockingQueue;

public class MediatorThread implements Runnable{
    private ItemsBuffer inputBuffer;
    private ItemsBuffer outputBuffer;

    public MediatorThread(ItemsBuffer inputBuffer, ItemsBuffer outputBuffer){
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            try {
                Item item = inputBuffer.take();
                outputBuffer.put(item);
                System.out.println("Mediator received & passed " + item);
                if (!item.isValid()){
                    System.out.println("Mediator finished work, because poissonPill received");
                    return;
                }
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
