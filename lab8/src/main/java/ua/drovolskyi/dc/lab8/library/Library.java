package ua.drovolskyi.dc.lab8.library;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

public class Library implements Serializable {
    private ArrayList<Author> authors = new ArrayList<>(); // replace by hashmap?
    private ArrayList<Book> books = new ArrayList<>();

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(!(o instanceof Library)){
            return false;
        }

        Library other = (Library) o;

        return authors.equals(other.authors) &&
                books.equals(other.books);
    }

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

    public boolean addAuthor(Author author){
        if (isAuthorExist(author.getId())){
            return false;
        }
        return authors.add(author);
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

    public boolean addBook(Book book){
        if (isBookExist(book.getISBN())){ // book is already exists
            return false;
        }

        Author author = null;
        try{
            author = getAuthorById(book.getAuthor().getId());
            book.setAuthor(author); // try to maintain book.author
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        return books.add(book);
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
            if(currentBook.getAuthor().getId() == author.getId()){
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

    public List<Author> getAllAuthors(){
        return this.authors;
    }

    public List<Book> getAllBooks(){
        return this.books;
    }

    public List<Book> getAllBooksFromAuthor(long authorId){
        List<Book> result = new LinkedList<>();

        for(Book book : books){
            if(book.getAuthor().getId() == authorId){
                result.add(book);
            }
        }

        return result;
    }

    public boolean changeAuthorName(long id, String newName){
        try{
            Author author = getAuthorById(id);
            author.setName(newName);
            return true;
        } catch(NoSuchElementException e){
            return false;
        }
    }

    public boolean changeBookTitle(long isbn, String newTitle){
        try{
            Book book = getBookByISBN(isbn);
            book.setTitle(newTitle);
            return true;
        } catch(NoSuchElementException e){
            return false;
        }
    }

    public boolean changeBookYear(long isbn, int year){
        try{
            Book book = getBookByISBN(isbn);
            book.setYear(year);
            return true;
        } catch(NoSuchElementException e){
            return false;
        }
    }

    public boolean changeBookNumberOfPages(long isbn, int pages){
        try{
            Book book = getBookByISBN(isbn);
            book.setNumberOfPages(pages);
            return true;
        } catch(NoSuchElementException e){
            return false;
        }
    }

    public boolean changeBookAuthor(long isbn, long authorId){
        try{
            Author author = getAuthorById(authorId);
            Book book = getBookByISBN(isbn);
            book.setAuthor(author);
            return true;
        } catch(NoSuchElementException e){
            return false;
        }
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

// + loading from XML
// + saving into XML

// + search author by ID
// + search book by ISBN

// + add new author
// + add new book for given author

// + remove author (and all him books)
// + remove book (if 0 books for given author, remove author or not?)

// + change parameters of author and book
// + for author: name
// + for book: title, year, numberOfPages, author

// + return full list of authors
// + return list of books for given author

/*
    Field 'author' in book is not always valid:
    it means, that book.author is not obviously points author object that exists in library
    But it is guaranteed that book.author.id == authorInLibrary.id

    NEED add .equals() method to books where only author ID is matters (not object)
 */
