package ua.edu.yarik.task_a;

public class HoneyPot {
    private final int capacity;
    private int honeyCount = 0;

    public HoneyPot(int capacity){
        this.capacity = capacity;
    }

    public boolean putHoney(){
        if (honeyCount == capacity){
            return false;
        }
        honeyCount++;
        return true;
    }

    public int getAllHoney(){
        if (isFull()){
            honeyCount = 0;
            return capacity;
        }
        return -1;
    }

    public boolean isEmpty(){
        return honeyCount == 0;
    }

    public boolean isFull(){
        return honeyCount == capacity;
    }
}
