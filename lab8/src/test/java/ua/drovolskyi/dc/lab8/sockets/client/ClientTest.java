package ua.drovolskyi.dc.lab8.sockets.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;
import ua.drovolskyi.dc.lab8.library.LibraryDB;
import ua.drovolskyi.dc.lab8.sockets.server.Server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientTest {
    private Client client;
    private LibraryDB db;
    private final int authorsNumber = 10;
    private final int booksNumber = 18;

    @BeforeAll
    public void setUp(){
        new Thread(()->{
            try {
                new Server(50000).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();

        try {
            Thread.sleep(1000); // need to sleep enough time to wait for server to start
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            client = new Client("localhost", 50000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            db = new LibraryDB();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    void addAuthor() throws IOException {
        // success
        for(int i = authorsNumber; i < authorsNumber + 100; i++){
            assertTrue(client.addAuthor(i, "author-" + i));
        }

        // failure, not unique ID
        for(int i = 0; i < authorsNumber; i++){
            assertFalse(client.addAuthor(i, "author-" + i));
        }
    }

    @Test
    void deleteAuthor() throws IOException, ClassNotFoundException {
        for(int i = 0; i < authorsNumber; i++){
            assertTrue(client.deleteAuthor(i));
            assertFalse(client.deleteAuthor(i));
        }
        assertTrue(client.getAllAuthors().isEmpty());
        assertTrue(client.getAllBooks().isEmpty()); // no authors, so no books

        for(int i = authorsNumber; i < 200*authorsNumber; i++){
            assertFalse(client.deleteAuthor(i));
            assertTrue(client.getAllBooksFromAuthor(i).isEmpty());
        }
    }

    @Test
    void addBook() throws IOException {
        // success
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertTrue(client.addBook(i, "book-"+i, 2000+i, 100+i, new Author((i-booksNumber)/2, "")));
        }

        // failure: not unique ID
        for(int i = 0; i < booksNumber; i++){
            assertFalse(client.addBook(i, "book-"+i, 2000+i, 100+i, new Author(i/2, "")));
        }

        // failure: append book to not-existent author
        for(int i = 2*booksNumber; i < 3*booksNumber; i++){
            assertFalse(client.addBook(i, "book-"+i, 2000+i, 100+i, new Author(authorsNumber + i, "")));
        }
    }

    @Test
    void deleteBook() throws IOException, ClassNotFoundException {
        for(int i = 0; i < booksNumber; i++){
            assertTrue(client.deleteBook(i));
            assertFalse(client.deleteBook(i));
        }
        assertTrue(client.getAllBooks().isEmpty());

        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(client.deleteBook(i));
        }
    }

    @Test
    void changeBookTitle() throws IOException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(client.changeBookTitle(i, "t-"+i));
            assertEquals("t-"+i, db.getBookByISBN(i).getTitle());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(client.changeBookTitle(i, "t-"+i));
        }
    }

    @Test
    void changeBookYear() throws IOException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(client.changeBookYear(i, i+5000));
            assertEquals(i+5000, db.getBookByISBN(i).getYear());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(client.changeBookYear(i, i+5000));
        }
    }

    @Test
    void changeBookNumberOfPages() throws IOException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(client.changeBookNumberOfPages(i, i+5000));
            assertEquals(i+5000, db.getBookByISBN(i).getNumberPages());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(client.changeBookNumberOfPages(i, i+5000));
        }
    }

    @Test
    void changeBookAuthor() throws IOException {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(client.changeBookAuthor(i, 0));
            assertEquals(0, db.getBookByISBN(i).getAuthor().getId());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(client.changeBookNumberOfPages(i, i+5000));
        }

        // changing to non-existent author
        for(int i = 0; i < booksNumber; i++){
            assertFalse(client.changeBookAuthor(i, i+5000));
        }
    }

    @Test
    void countBooks() throws IOException {
        assertEquals(db.getNumberOfBooks(), client.getNumberOfBooks());
    }

    @Test
    void getAllBooks() throws IOException, ClassNotFoundException {
        assertEquals(db.getAllBooks(), client.getAllBooks());
    }

    @Test
    void getAllBooksFromAuthor() throws IOException, ClassNotFoundException {
        for(int i = 0; i < authorsNumber; i++){
            List<Book> actualBooks = client.getAllBooksFromAuthor(i);
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
    void getAllAuthors() throws IOException, ClassNotFoundException {
        assertEquals(db.getAllAuthors(), client.getAllAuthors());
    }
}