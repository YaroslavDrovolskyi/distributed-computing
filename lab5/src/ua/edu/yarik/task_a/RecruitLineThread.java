package ua.edu.yarik.task_a;

public class RecruitLineThread implements Runnable{
    Direction[] recruitsDirection;
    MyCyclicBarrier barrier;


    public RecruitLineThread(int n, MyCyclicBarrier barrier){
        this.barrier = barrier;
        this.recruitsDirection = new Direction[n];

        for (int i = 0; i < recruitsDirection.length; i++){
            recruitsDirection[i] = Direction.randomDirection();
        }
    }


    @Override
    public void run(){
        boolean stop = false;
        int iterationsCount = 0;

//        printRecruits();
        while(!stop){
            stop = true;
            iterationsCount++;

            for (int i = 0; i < recruitsDirection.length - 1; i++){
                if(recruitsDirection[i] == Direction.RIGHT &&
                        recruitsDirection[i + 1] == Direction.LEFT){
                    recruitsDirection[i] = Direction.LEFT;
                    recruitsDirection[i + 1] = Direction.RIGHT;
                    stop = false;
                }
            }

//            printRecruits(); ///////

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(Thread.currentThread().getName() +
                ": stabilized after " + iterationsCount + " iterations");

        try {
            System.out.println(Thread.currentThread().getName() + " arrived to barrier");
            barrier.await();

            System.out.println(Thread.currentThread().getName() + " finished");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public void printRecruits(){
        System.out.print("[ ");
        for (Direction r : recruitsDirection){
            System.out.print(r + " ");
        }
        System.out.println("]");
    }





}
