package ua.edu.yarik.task_a;

public class Program {
    public static void main(String[] args) {
        MyCyclicBarrier barrier = new MyCyclicBarrier(5);
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++){
            threads[i] = new Thread(new RecruitLineThread(50, barrier));
        }

        for (Thread t : threads){
            t.start();
        }

    }
}
