package ua.drovolskyi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Library {
    private final int SIZE; // is number of books in library
    private int[] books; // books[i] is number of book with id = i in library

    public Library(int size){
        this.SIZE = size;
        books = new int[size];
        Arrays.fill(books, 1);
    }

    public int getSize(){
        return SIZE;
    }

    public boolean isBookAvailable(int id){
        if (id >= SIZE){
            throw new IndexOutOfBoundsException("id must be less than size");
        }
        return books[id] > 0;
    }

    public int takeBook(int id){
        if (!isBookAvailable(id)){
            throw new RuntimeException("no such book");
        }
        books[id]--;
        return id;
    }

    public void returnBook(int id){
        if (id >= SIZE){
            throw new IndexOutOfBoundsException("id must be less than size");
        }
        books[id]++;
    }

    public List<Integer> getAvailableBooks(){
        List<Integer> availableBooks = new LinkedList<>();
        for (int i = 0; i < SIZE; i++){
            if (books[i] > 0){
                availableBooks.add(i);
            }
        }
        return availableBooks;
    }

    public List<Integer> getAllPossibleBooks(){
        List<Integer> allBooks = new LinkedList<>();
        for (int i = 0; i < SIZE; i++){
            allBooks.add(i);
        }
        return allBooks;
    }
}
