package ua.drovolskyi;

import java.util.concurrent.locks.ReentrantLock;
/*
    Module1 variant #2
 */
public class Program {
    public static void main(String[] args) {
        // one reader will want to take 3 books at time, so
        // size of library must be >= 3
        Library library = new Library(15);
        Thread readers[] = new Thread[5];
        ReentrantLock libraryLock = new ReentrantLock();

        // create readers
        for (int i = 0; i < 5; i++){
            readers[i] = new Thread(new Reader(library, libraryLock));
        }

        // start readers
        for (Thread reader : readers){
            reader.start();
        }
    }
}
