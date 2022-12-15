package ua.drovolskyi.dc.lab8.rmi.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;
import ua.drovolskyi.dc.lab8.library.LibraryDB;
import ua.drovolskyi.dc.lab8.sockets.client.Client;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryRMIImplTest {
    private LibraryRMIImpl lib;
    private LibraryDB db;
    private final int authorsNumber = 10;
    private final int booksNumber = 18;

    public LibraryRMIImplTest() throws RemoteException, SQLException, FileNotFoundException {
        lib = new LibraryRMIImpl();
        db = new LibraryDB();
    }

    @BeforeEach
    void init(){
        db.deleteAllBooks();
        db.deleteAllAuthors();

        // add authors
        for(int i = 0; i < authorsNumber; i++){
            db.addAuthor(i, "author-" + i);
        }

        // add books
        for(int i = 0; i < booksNumber; i++){
            db.addBook(i, "book-"+i, 2000+i, 100+i, i/2);
        }
    }


    @Test
    void addAuthor() throws RemoteException {
        // success
        for(int i = authorsNumber; i < authorsNumber + 100; i++){
            assertTrue(lib.addAuthor(i, "author-" + i));
        }

        // failure, not unique ID
        for(int i = 0; i < authorsNumber; i++){
            assertFalse(lib.addAuthor(i, "author-" + i));
        }
    }

    @Test
    void deleteAuthor() throws RemoteException {
        for(int i = 0; i < authorsNumber; i++){
            assertTrue(lib.deleteAuthor(i));
            assertFalse(lib.deleteAuthor(i));
        }
        assertTrue(lib.getAllAuthors().isEmpty());
        assertTrue(lib.getAllBooks().isEmpty()); // no authors, so no books

        for(int i = authorsNumber; i < 200*authorsNumber; i++){
            assertFalse(lib.deleteAuthor(i));
            assertTrue(lib.getAllBooksFromAuthor(i).isEmpty());
        }
    }

    @Test
    void addBook() throws RemoteException {
        // success
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertTrue(lib.addBook(i, "book-"+i, 2000+i, 100+i, (i-booksNumber)/2));
        }

        // failure: not unique ID
        for(int i = 0; i < booksNumber; i++){
            assertFalse(lib.addBook(i, "book-"+i, 2000+i, 100+i, i/2));
        }

        // failure: append book to not-existent author
        for(int i = 2*booksNumber; i < 3*booksNumber; i++){
            assertFalse(lib.addBook(i, "book-"+i, 2000+i, 100+i, authorsNumber + i));
        }
    }

    @Test
    void deleteBook() throws RemoteException {
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.deleteBook(i));
            assertFalse(lib.deleteBook(i));
        }
        assertTrue(lib.getAllBooks().isEmpty());

        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(lib.deleteBook(i));
        }
    }

    @Test
    void changeBookTitle() throws RemoteException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookTitle(i, "t-"+i));
            assertEquals("t-"+i, db.getBookByISBN(i).getTitle());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(lib.changeBookTitle(i, "t-"+i));
        }
    }

    @Test
    void changeBookYear() throws RemoteException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookYear(i, i+5000));
            assertEquals(i+5000, db.getBookByISBN(i).getYear());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(lib.changeBookYear(i, i+5000));
        }
    }

    @Test
    void changeBookNumberOfPages() throws RemoteException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookNumberOfPages(i, i+5000));
            assertEquals(i+5000, db.getBookByISBN(i).getNumberPages());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(lib.changeBookNumberOfPages(i, i+5000));
        }
    }

    @Test
    void changeBookAuthor() throws RemoteException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookAuthor(i, 0));
            assertEquals(0, db.getBookByISBN(i).getAuthor().getId());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(lib.changeBookNumberOfPages(i, i+5000));
        }

        // changing to non-existent author
        for(int i = 0; i < booksNumber; i++){
            assertFalse(lib.changeBookAuthor(i, i+5000));
        }
    }

    @Test
    void countBooks() throws RemoteException {
        assertEquals(db.getNumberOfBooks(), lib.countBooks());
    }

    @Test
    void getAllBooks() throws RemoteException {
        assertEquals(db.getAllBooks(), lib.getAllBooks());
    }

    @Test
    void getAllBooksFromAuthor() throws RemoteException {
        for(int i = 0; i < authorsNumber; i++){
            List<Book> actualBooks = lib.getAllBooksFromAuthor(i);
            List<Book> books = db.getAllBooksFromAuthor(i);

            int j = 0;
            for(Book book : books){
                if(book.getAuthor().getId() == i){
                    assertEquals(book, actualBooks.get(j));
                    j++;
                }
            }
        }
    }

    @Test
    void getAllAuthors() throws RemoteException {
        assertEquals(db.getAllAuthors(), lib.getAllAuthors());
    }
}