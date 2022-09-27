package ua.edu.yarik.task_b;

public class Garden {
    private int[][] cells; // water level in cell
    protected int size;

    public Garden(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size of garden must be > 0");
        }
        this.size = size;
        this.cells = new int[size][size];
    }

    public int getWaterState(int i, int j) {
        return cells[i][j];
    }

    public void setWaterState(int i, int j, int value) {
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value must be in interval [0,9]");
        }
        cells[i][j] = value;
    }

    public int getSize() {
        return this.size;
    }
}
