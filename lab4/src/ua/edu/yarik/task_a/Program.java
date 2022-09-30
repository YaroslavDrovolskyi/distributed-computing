package ua.edu.yarik.task_a;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Program {
    public static void main(String[] args) {
        MyReentrantReadWriteLock lock = new MyReentrantReadWriteLock();
        String filepath = "D:/Repositories/distributed-computing/lab4/src/resources/PhonesList.txt";

        FileRecordsWriter writer1 = new FileRecordsWriter("writer-1", filepath, lock,
                generateListToWrite(0, 1000));
        FileRecordsWriter writer2 = new FileRecordsWriter("writer-2", filepath, lock,
                generateListToWrite(1001, 2000));
        FileRecordsWriter writer3 = new FileRecordsWriter("writer-3", filepath, lock,
                generateListToWrite(2001, 3000));

        FileRecordsRemover remover1 = new FileRecordsRemover("remover-1", filepath, lock,
                "name100");
        FileRecordsRemover remover2 = new FileRecordsRemover("remover-2", filepath, lock,
                "name1000");
        FileRecordsRemover remover3 = new FileRecordsRemover("remover-3", filepath, lock,
                "name2000");

        NameFinder nameFinder1 = new NameFinder("nameFinder-1", filepath,
                new PhoneNumber("phone1"), lock);
        NameFinder nameFinder2 = new NameFinder("nameFinder-2", filepath,
                new PhoneNumber("phone2"), lock);
        NameFinder nameFinder3 = new NameFinder("nameFinder-3", filepath,
                new PhoneNumber("phone3"), lock);

        PhonesFinder phonesFinder1 = new PhonesFinder("phonesFinder-1", filepath,
                "name1500", lock);
        PhonesFinder phonesFinder2 = new PhonesFinder("phonesFinder-2", filepath,
                "name2000", lock);
        PhonesFinder phonesFinder3 = new PhonesFinder("phonesFinder-3", filepath,
                "name2500", lock);



        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {

            executor.submit(writer1);
            executor.submit(nameFinder1);
            executor.submit(nameFinder2);
            executor.submit(phonesFinder1);
            executor.submit(remover1);
            Thread.sleep(10000);

            executor.submit(writer2);
            executor.submit(nameFinder2);
            executor.submit(nameFinder3);
            executor.submit(phonesFinder2);
            executor.submit(remover2);
            Thread.sleep(10000);

            executor.submit(writer3);
            executor.submit(nameFinder1);
            executor.submit(nameFinder3);
            executor.submit(phonesFinder3);
            executor.submit(remover3);
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<String> generateListToWrite(int start, int end){
        List<String> result = new LinkedList<String>();
        for (int i = start; i <= end; i++){
            String c = "name" + String.valueOf(i) +
                    " : phone" + String.valueOf(i);

            result.add(c);
        }

        return result;
    }

}
