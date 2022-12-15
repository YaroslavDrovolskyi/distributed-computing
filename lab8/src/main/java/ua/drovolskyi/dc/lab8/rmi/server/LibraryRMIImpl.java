package ua.drovolskyi.dc.lab8.rmi.server;

import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;
import ua.drovolskyi.dc.lab8.library.LibraryDB;
import ua.drovolskyi.dc.lab8.rmi.interfaces.LibraryRMI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LibraryRMIImpl extends UnicastRemoteObject
        implements LibraryRMI {
    private LibraryDB db;

    // need to sync on DB, because RMI can run methods in multiple threads
    private ReadWriteLock dbLock;

    public LibraryRMIImpl() throws RemoteException {
        super();

        try {
            db = new LibraryDB();
            dbLock = new ReentrantReadWriteLock();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addAuthor(long id, String name) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.addAuthor(id, name);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean deleteAuthor(long id) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.deleteAuthor(id);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean addBook(long isbn, String title, int year, int pages, long authorId)
            throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.addBook(isbn, title, year, pages, authorId);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean deleteBook(long isbn) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.deleteBook(isbn);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean changeBookTitle(long isbn, String newTitle) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.changeBookTitle(isbn, newTitle);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean changeBookYear(long isbn, int year) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.changeBookYear(isbn, year);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean changeBookNumberOfPages(long isbn, int pages) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.changeBookNumberOfPages(isbn, pages);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public boolean changeBookAuthor(long isbn, long authorId) throws RemoteException {
        dbLock.writeLock().lock();
        boolean result = db.changeBookAuthor(isbn, authorId);
        dbLock.writeLock().unlock();

        return result;
    }

    @Override
    public int countBooks() throws RemoteException {
        dbLock.readLock().lock();
        int numberOfBooks = db.getNumberOfBooks();
        dbLock.readLock().unlock();

        return numberOfBooks;
    }

    @Override
    public List<Book> getAllBooks() throws RemoteException {
        dbLock.readLock().lock();
        List<Book> books = db.getAllBooks();
        dbLock.readLock().unlock();

        return books;
    }

    @Override
    public List<Book> getAllBooksFromAuthor(long authorId) throws RemoteException {
        dbLock.readLock().lock();
        List<Book> books = db.getAllBooksFromAuthor(authorId);
        dbLock.readLock().unlock();

        return books;
    }

    @Override
    public List<Author> getAllAuthors() throws RemoteException {
        dbLock.readLock().lock();
        List<Author> authors = db.getAllAuthors();
        dbLock.readLock().unlock();

        return authors;
    }
}
