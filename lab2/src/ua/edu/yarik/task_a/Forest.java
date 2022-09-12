package ua.edu.yarik.task_a;

import java.util.Random;

public class Forest {
    private final int size;
    private final int bearPositionX;
    private final int bearPositionY;
    int[][] cells;


    public Forest(int size){
        this.size = size;
        this.cells = new int[size][size];

        Random rand = new Random();
        bearPositionX = rand.nextInt(size);
        bearPositionY = rand.nextInt(size);
    }

    public int getSize(){
        return this.size;
    }
    public boolean isBearPosition(int x, int y){
        return (x == bearPositionX) && (y == bearPositionY);
    }
}
