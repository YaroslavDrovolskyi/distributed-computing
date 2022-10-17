package ua.drovolskyi;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Reader implements Runnable{
    private final Library library;
    private List<Integer> wantedBooks = new LinkedList<>();
    private List<Integer> takenBooks = new LinkedList<>();
    ReentrantLock libraryLock;

    public Reader(Library library, ReentrantLock libraryLock){
        this.library = library;
        this.libraryLock = libraryLock;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            try{
                libraryLock.lock();

                // return previously taken books
                for (int book : takenBooks){
                    library.returnBook(book);
                }

                System.out.print(Thread.currentThread().getName() +
                        " returned: " + Arrays.toString(takenBooks.toArray()));
                takenBooks.clear(); // clear, because we returned all taken books

                // generate wanted books
                List<Integer> allBooks = library.getAllPossibleBooks();
                Collections.shuffle(allBooks);
                wantedBooks.addAll(new LinkedList<Integer>(allBooks.subList(0, 3)));

                // take wanted books if available
                for (int wantedBook : wantedBooks){
                    if (library.isBookAvailable(wantedBook)){
                        takenBooks.add(library.takeBook(wantedBook));
                    }
                    Thread.sleep(1000);
                }


                // print
                System.out.println(
                        " wanted: " + Arrays.toString(wantedBooks.toArray()) +
                        ", taken: " + Arrays.toString(takenBooks.toArray()) +
                        ", available in library: " + Arrays.toString(library.getAvailableBooks().toArray()));


                wantedBooks.clear();
            } catch(InterruptedException e){
                System.out.println(Thread.currentThread().getName() + " interrupted");
                return;
            } finally{
                libraryLock.unlock();
            }


            try {
                Thread.sleep(3000); // read taken books
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted");
                return;
            }
        }
    }
}
