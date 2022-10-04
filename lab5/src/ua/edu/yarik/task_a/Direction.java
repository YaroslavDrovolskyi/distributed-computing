package ua.edu.yarik.task_a;

import java.util.Random;

public enum Direction {
    LEFT,
    RIGHT;


    private static final Random rand = new Random();

    public static Direction randomDirection(){
        Direction[] directions = values();
        return directions[rand.nextInt(directions.length)];
    }

    @Override
    public String toString() {
        switch(this) {
            case LEFT: return "<-";
            case RIGHT: return "->";
            default: throw new IllegalArgumentException();
        }
    }
}
