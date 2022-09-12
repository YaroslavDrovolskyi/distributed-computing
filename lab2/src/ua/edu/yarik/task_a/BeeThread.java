package ua.edu.yarik.task_a;

import java.time.LocalDateTime;

public class BeeThread implements Runnable{
    private TaskManager manager; // synchronizator
    private Forest forest; // shared data

    public BeeThread(TaskManager manager, Forest forest){
        this.manager = manager;
        this.forest = forest;
    }

    @Override
    public void run() {
        while(Thread.interrupted() == false){
            int rowNumber = manager.getTask();
            if (rowNumber == -1){ // no task to do
                System.out.println(Thread.currentThread().getName() + " exits, because no tasks to do");
                return;
            }

            System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName() + " got task " + rowNumber);
            try {
                Thread.sleep(1000); // look for a bear
                boolean result = false;
                int size = forest.getSize();
                for (int i = 0; i < size && !result; i++){
                    result = forest.isBearPosition(rowNumber, i);
                }
                if (result){
                    Thread.sleep(2000); // punish the bear
                    System.out.println(LocalDateTime.now() + " "+ Thread.currentThread().getName() + " found bear at row " + rowNumber);
                }
                manager.sendResult(result);

            } catch (InterruptedException e) {
                return;
            }
        }
    }
}

