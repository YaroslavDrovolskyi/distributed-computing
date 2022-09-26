package ua.edu.yarik.task_a;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class MyReentrantReadWriteLock {
    private Map<Thread, Integer> readers = new HashMap<>();
    private Thread writer = null;
    private int writerPermitsCount = 0;
    private int writeRequestsCount = 0;

    /* Read */
    public synchronized void readLock() throws InterruptedException {
        Thread t = Thread.currentThread();
        while(!canPermitReader(t)){
            wait();
        }
        permitReader(t);
    }

    private boolean canPermitReader(Thread t){
        if (isWriter(t) || isReader(t)){
            return true;
        }
        if (writer == null && writeRequestsCount == 0){
            return true;
        }
        return false;
    }

    private void permitReader(Thread t){
        int readerPermitsCounter = getReaderPermitsCounter(t);
        readers.put(t, readerPermitsCounter + 1);
    }

    public synchronized void readUnlock(){
        cancelReaderPermission(Thread.currentThread());
        notifyAll();
    }

    private void cancelReaderPermission(Thread t){
        if (!isReader(t)){
            throw new IllegalStateException("readUnlock(): " + t.getName() + " is not a reader");
        }

        int readerPermitsCount = getReaderPermitsCounter(t);
        if (readerPermitsCount == 1){
            readers.remove(t);
        }
        else{
            readers.put(t, readerPermitsCount - 1);
        }
    }

    private int getReaderPermitsCounter(Thread t){
        if (isReader(t)){
            return readers.get(t);
        }
        return 0;
    }

    private boolean isReader(Thread t){
        return readers.containsKey(t);
    }


    /* Write */
    public synchronized void writeLock() throws InterruptedException {
        writeRequestsCount++;
        Thread t = Thread.currentThread();
        while(!canPermitWriter(t)){
            wait();
        }
        writeRequestsCount--;
        permitWriter(t);
    }

    private boolean canPermitWriter(Thread t){
        if (isWriter(t)){
            return true;
        }
        if (isReader(t) && readers.size() == 1){ // if it is only reader
            return true;
        }
        if (readers.size() == 0 && writer == null){
            return true;
        }
        return false;
    }

    private void permitWriter(Thread t){
        writer = t;
        writerPermitsCount++;
    }

    public synchronized void writeUnlock(){
        cancelWriterPermission(Thread.currentThread());
        notifyAll();
    }

    private void cancelWriterPermission(Thread t) {
        if (!isWriter(t)){
            throw new IllegalStateException("writeUnlock(): " + t.getName() + " is not a writer");
        }

        writerPermitsCount--;
        if (writerPermitsCount == 0){
            writer = null;
        }

    }

    private boolean isWriter(Thread t){
        return writer == t;
    }
}

/*
    Example: https://jenkov.com/tutorials/java-concurrency/read-write-locks.html
 */
