package ua.edu.yarik.task_b;

public class Item {
    private int price;

    public Item(int price){
        this.price = price;
    }

    public int getPrice(){
        return this.price;
    }

    public boolean isValid(){
        return price > 0;
    }

    @Override
    public String toString(){
        return "Item {price = " + price + "}";
    }

}
