package ua.edu.yarik.task_b;

import java.util.LinkedList;

public class ItemsBuffer {
    private int capacity;
    private LinkedList<Item> data;

    public ItemsBuffer(int capacity){
        this.capacity = capacity;
        this.data = new LinkedList<Item>();
    }

    public synchronized void put(Item i){
        while(data.size() == capacity){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        data.add(i);
        notify();
    }

    public synchronized Item take(){
        while(data.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Item result = data.remove();
        notify();
        return result;
    }


}
