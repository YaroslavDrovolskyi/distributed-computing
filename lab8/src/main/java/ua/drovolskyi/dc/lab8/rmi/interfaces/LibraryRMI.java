package ua.drovolskyi.dc.lab8.rmi.interfaces;

import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

public interface LibraryRMI extends Remote {
    boolean addAuthor(long id, String name) throws RemoteException, IOException, ClassNotFoundException;

    boolean deleteAuthor(long id) throws RemoteException, IOException;

    boolean addBook(long isbn, String title, int year, int pages, long authorId)
            throws RemoteException, IOException, ClassNotFoundException;

    boolean deleteBook(long isbn) throws RemoteException, IOException;
    boolean changeBookTitle(long isbn, String newTitle) throws RemoteException;
    boolean changeBookYear(long isbn, int year) throws RemoteException;
    boolean changeBookNumberOfPages(long isbn, int pages) throws RemoteException;
    boolean changeBookAuthor(long isbn, long authorId) throws RemoteException;

    int countBooks() throws RemoteException, IOException;
    List<Book> getAllBooks() throws RemoteException, IOException;
    List<Book> getAllBooksFromAuthor(long authorId) throws RemoteException, IOException;
    List<Author> getAllAuthors() throws RemoteException, IOException;
}
