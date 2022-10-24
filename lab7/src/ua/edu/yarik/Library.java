package ua.edu.yarik;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Library {
    private ArrayList<Author> authors = new ArrayList<>(); // replace by hashmap?
    private ArrayList<Book> books = new ArrayList<>();

//    public saveToFile(); // to be implemented
//    public LoadFromFile(); // to be implemented

    public boolean isAuthorExist(long id){
        for(Author a : authors){
            if (a.getId() == id){
                return true;
            }
        }
        return false;
    }

    public boolean isBookExist(long isbn){
        for(Book b : books){
            if (b.getISBN() == isbn){
                return true;
            }
        }
        return false;
    }

    public Author getAuthorById(long id){
        for(Author a : authors){
            if (a.getId() == id){
                return a;
            }
        }
        throw new NoSuchElementException("No author with given id: " + id);
    }

    public Book getBookByISBN(long isbn){
        for(Book b : books){
            if (b.getISBN() == isbn){
                return b;
            }
        }
        throw new NoSuchElementException("No book with given ISBN: " + isbn);
    }

    public boolean addAuthor(long id, String name){
        if (isAuthorExist(id)){
            return false;
        }
        return authors.add(new Author(id, name));
    }

    public boolean addBook(long isbn, String title, int year, int pages, long authorId){
        if (isBookExist(isbn)){ // book is already exists
            return false;
        }

        Author author = null;
        try{
            author = getAuthorById(authorId);
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        return books.add(new Book(isbn, title, year, pages, author));
    }

    public boolean deleteAuthor(long id){
        Author author = null;
        try{
            author = getAuthorById(id);
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        authors.remove(author);

        // remove books wrote by this author
        Iterator<Book> it = books.iterator();
        while(it.hasNext()){
            Book currentBook = it.next();
            if(currentBook.getAuthor() == author){
                it.remove();
            }
        }
        return true;
    }

    // does not remove author if 0 books wrote by him
    public boolean deleteBook(long isbn){
        Book book = null;
        try{
            book = getBookByISBN(isbn);
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        books.remove(book);
        return true;
    }

    public void print(){
        System.out.println("==================== Library ====================");

        System.out.println(">>>>> Authors:");
        if(authors.isEmpty()){
            System.out.println("no authors");
        }
        for(Author a : authors){
            System.out.println(a);
        }

        System.out.println("\n>>>>> Books:");
        if(books.isEmpty()){
            System.out.println("no books");
        }
        for(Book b : books){
            System.out.println(b);
        }

        System.out.println("=================================================");
    }



}


// loading from XML
// upload into XML

// + search author by ID
// + search book by ISBN

// + add new author
// + add new book for given author

// + remove author (and all him books)
// + remove book (if 0 books for given author, remove author or not?)

// change parameters of author and book

// return full list of authors
// return list of books for given author
