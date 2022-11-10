package ua.edu.yarik.dc.lab7;

import org.junit.jupiter.api.*;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


class LibraryTest {
    private Library lib = new Library();
    private List<Author> authors = new LinkedList<>();
    private List<Book> books = new LinkedList<>();
    private final int authorsNumber = 10;
    private final int booksNumber = 18;

    public LibraryTest(){

    }

    @BeforeEach
    void init(){
        // add authors
        for(int i = 0; i < authorsNumber; i++){
            lib.addAuthor(i, "author-" + i);
            authors.add(new Author(i,"author-" + i));
        }

        // add books
        for(int i = 0; i < booksNumber; i++){
            lib.addBook(i, "book-"+i, 2000+i, 100+i, i/2);
            books.add(new Book(i, "book-"+i, 2000+i, 100+i, new Author(i/2, "")));
        }
    }

    @Test
    void addAuthor() {
        // success
        for(int i = authorsNumber; i < authorsNumber + 100; i++){
            assertTrue(lib.addAuthor(i, "author-" + i));
            authors.add(new Author(i,"author-" + i));
        }

        // failure, not unique ID
        for(int i = 0; i < authorsNumber; i++){
            assertFalse(lib.addAuthor(i, "author-" + i));
        }
    }

    @Test
    void isAuthorExist() {
        for(int i = 0; i < authorsNumber; i++){
            assertTrue(lib.isAuthorExist(i));
        }
        for(int i = authorsNumber; i < 2*authorsNumber; i++){
            assertFalse(lib.isAuthorExist(i));
        }
    }

    @Test
    void getAuthorById() {
        // get existent items
        for(int i = 0; i < authorsNumber; i++){
            assertEquals(authors.get(i), lib.getAuthorById(i));
        }

        // get non-existent items
        for(int i = authorsNumber; i < 2*authorsNumber; i++){
            final int id = i;
            Throwable e = assertThrows(NoSuchElementException.class,
                    () -> {
                        lib.getAuthorById(id);
                    }
            );
            assertEquals("No author with given id: " + id, e.getMessage());
        }
    }

    @Test
    void getAllAuthors() {
        List<Author> actualAuthors = lib.getAllAuthors();
        assertEquals(authors, actualAuthors);
    }

    @Test
    void changeAuthorName() {
        // success
        for(int i = 0; i < authorsNumber; i++){
            assertTrue(lib.changeAuthorName(i, "name-"+i));
            assertEquals("name-"+i, lib.getAuthorById(i).getName());
        }

        // changing not-existent authors
        for(int i = authorsNumber; i < 2*authorsNumber; i++){
            assertFalse(lib.changeAuthorName(i, "name-"+i));
        }
    }

    @Test
    void deleteAuthor() {
        for(int i = 0; i < authorsNumber; i++){
            assertTrue(lib.deleteAuthor(i));
            assertFalse(lib.deleteAuthor(i));
        }
        assertTrue(lib.getAllAuthors().isEmpty());
        assertTrue(lib.getAllBooks().isEmpty());// no authors, so no books

        for(int i = authorsNumber; i < 200*authorsNumber; i++){
            assertFalse(lib.deleteAuthor(i));
            assertTrue(lib.getAllBooksFromAuthor(i).isEmpty());
        }

    }



    @Test
    void addBook() {
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
    void isBookExist() {
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.isBookExist(i));
        }
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(lib.isBookExist(i));
        }
    }


    @Test
    void getBookByISBN() {
        // get existent items
        for(int i = 0; i < booksNumber; i++){
            assertEquals(books.get(i), lib.getBookByISBN(i));
        }

        // get non-existent items
        for(int i = booksNumber; i < 2*booksNumber; i++){
            final int id = i;
            Throwable e = assertThrows(NoSuchElementException.class,
                    () -> {
                        lib.getBookByISBN(id);
                    }
            );
            assertEquals("No book with given ISBN: " + id, e.getMessage());
        }
    }

    @Test
    void getAllBooks(){
        List<Book> actualBooks = lib.getAllBooks();
        assertEquals(books, actualBooks);
    }

    @Test
    void getAllBooksFromAuthor(){
        for(int i = 0; i < authorsNumber; i++){
            List<Book> actualBooks = lib.getAllBooksFromAuthor(i);
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
    void changeBookTitle() {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookTitle(i, "t-"+i));
            assertEquals("t-"+i, lib.getBookByISBN(i).getTitle());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(lib.changeBookTitle(i, "t-"+i));
        }
    }

    @Test
    void changeBookYear() {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookYear(i, i+5000));
            assertEquals(i+5000, lib.getBookByISBN(i).getYear());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(lib.changeBookYear(i, i+5000));
        }
    }

    @Test
    void changeBookNumberOfPages() {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookNumberOfPages(i, i+5000));
            assertEquals(i+5000, lib.getBookByISBN(i).getNumberPages());
        }

        // changing non-existent books
        for(int i = booksNumber; i < 20*booksNumber; i++){
            assertFalse(lib.changeBookNumberOfPages(i, i+5000));
        }
    }

    @Test
    void changeBookAuthor() {
        // success
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.changeBookAuthor(i, 0));
            assertEquals(0, lib.getBookByISBN(i).getAuthor().getId());
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
    void deleteBook() {
        for(int i = 0; i < booksNumber; i++){
            assertTrue(lib.deleteBook(i));
            assertFalse(lib.deleteBook(i));
        }
        assertTrue(lib.getAllBooks().isEmpty());

        for(int i = booksNumber; i < 2*booksNumber; i++){
            assertFalse(lib.deleteBook(i));
        }
    }
}