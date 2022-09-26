package ua.edu.yarik.task_a;

public class Program {
    public static void main(String[] args) {
        MyReentrantReadWriteLock lock = new MyReentrantReadWriteLock();
        PhonesFinder f = new PhonesFinder(
                "D:/Repositories/distributed-computing/lab4/src/resources/PhonesList.txt", "r3", lock);
        try {
            f.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
