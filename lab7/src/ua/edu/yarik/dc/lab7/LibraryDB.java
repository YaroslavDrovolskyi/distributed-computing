package ua.edu.yarik.dc.lab7;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LibraryDB {
    private Connection connection;

    public LibraryDB() throws FileNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/library";
        String username = "root";
        Scanner passwordScanner = new Scanner(new File("D:\\passwords\\mySql.txt"));
        String password = passwordScanner.nextLine();
        connection = DriverManager.getConnection(url, username, password);
    }

    public boolean addAuthor(long id, String name) throws SQLException {
        Statement s = connection.createStatement();
        String query = "INSERT INTO AUTHORS VALUES(" +
                id + ", " +
                "'" + name + "')";
        int affectedRows = s.executeUpdate(query);
        s.close();

        return affectedRows > 0;
    }

    public boolean addBook(long isbn, String title, int year, int pages, long authorId) throws SQLException {
        Statement s = connection.createStatement();
        String query = "INSERT INTO BOOKS VALUES(" +
                isbn + ", " +
                "'" + title + "', " +
                year + ", " +
                pages + ", " +
                authorId + ")";
        int affectedRows = s.executeUpdate(query);
        s.close();

        return affectedRows > 0;
    }


    public void close() throws SQLException {
        connection.close();
    }
}

// change book id to isbn

// loading from XML
// upload into XML

// search author by ID
// search book by ISBN

// add new author
// add new book for given author

// remove author (and all him books)
// remove book (if 0 books for given author, remove author or not?)

// change parameters of author and book

// return full list of authors
// return list of books for given author
