package ua.edu.yarik.task_a;

public class Program {
    public static void main(String[] args) {
//        MyBarrier barrier = new MyBarrier(2);

        /*
        Runnable th1 = () -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable th2 = () -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable th3 = () -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable th4 = () -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread t1 = new Thread(th1);
        Thread t2 = new Thread(th2);
        Thread t3 = new Thread(th3);
        Thread t4 = new Thread(th4);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

         */


        MyBarrier barrier = new MyBarrier(5);
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++){
            threads[i] = new Thread(new RecruitLineThread(50, barrier));
        }

        for (Thread t : threads){
            t.start();
        }

        /*
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

         */

    }
}
