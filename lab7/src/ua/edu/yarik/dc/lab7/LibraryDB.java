package ua.edu.yarik.dc.lab7;

import javax.xml.transform.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LibraryDB {
    private Connection connection;
    private Statement statement;

    public LibraryDB() throws FileNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/library";
        String username = "root";
        Scanner passwordScanner = new Scanner(new File("D:\\passwords\\mySql.txt"));
        String password = passwordScanner.nextLine();
        connection = DriverManager.getConnection(url, username, password);
        statement = connection.createStatement();
    }

    public boolean addAuthor(long id, String name){
        String query = "INSERT INTO authors VALUES(" +
                id + ", " +
                "'" + name + "')";
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch(SQLException e){
            return false;
        }
    }

    public boolean addBook(long isbn, String title, int year, int pages, long authorId){
        String query = "INSERT INTO books VALUES(" +
                isbn + ", " +
                "'" + title + "', " +
                year + ", " +
                pages + ", " +
                authorId + ")";
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch(SQLException e){
            return false;
        }
    }

    public boolean deleteAllAuthors(){
        List<Author> authors = getAllAuthors();

        for(Author a : authors){
            deleteAuthor(a.getId());
        }

        return true;
    }

    public boolean deleteAllBooks(){
        String query = "TRUNCATE TABLE books";
        try{
            statement.executeUpdate(query);
            return true;
        } catch(SQLException e){
            return false;
        }
    }

    public boolean deleteAuthor(long id){
        String query = "DELETE FROM authors WHERE id = " + id;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch(SQLException e){
            return false;
        }
    }

    public boolean deleteBook(long isbn){
        String query = "DELETE FROM books WHERE id = " + isbn;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch(SQLException e){
            return false;
        }
    }

    public Author getAuthorById(long id){
        String query = "SELECT * FROM authors WHERE id = " + id;
        try{
            ResultSet result = statement.executeQuery(query);
            if(result.next()){
                return new Author(result.getLong("id"), result.getString("name"));
            }
        } catch(SQLException e){
            throw new NoSuchElementException(e);
        }

        throw new NoSuchElementException("No author with given id: " + id);
    }

    public Book getBookByISBN(long isbn){
        String query = "SELECT * FROM books WHERE id = " + isbn;
        try{
            ResultSet result = statement.executeQuery(query);
            if(result.next()){
                return new Book(result.getLong("id"),
                        result.getString("title"),
                        result.getInt("year"),
                        result.getInt("number_of_pages"),
                        new Author(result.getLong("id_author"), "")
                );
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        throw new NoSuchElementException("No book with given ISBN: " + isbn);
    }

    public boolean changeAuthorName(long id, String newName){
        String query = "UPDATE authors SET name = '" + newName + "' WHERE id = " + id;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    public boolean changeBookTitle(long isbn, String newTitle){
        String query = "UPDATE books SET title = '" + newTitle + "' WHERE id = " + isbn;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    public boolean changeBookYear(long isbn, int year){
        String query = "UPDATE books SET year = " + year + " WHERE id = " + isbn;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    public boolean changeBookNumberOfPages(long isbn, int pages){
        String query = "UPDATE books SET number_of_pages = " + pages + " WHERE id = " + isbn;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    public boolean changeBookAuthor(long isbn, long authorId){
        String query = "UPDATE books SET id_author = " + authorId + " WHERE id = " + isbn;
        try{
            int affectedRows = statement.executeUpdate(query);
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    public List<Author> getAllAuthors(){
        String query = "SELECT * FROM authors";
        List<Author> authors = new LinkedList<>();

        try{
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                authors.add(new Author(result.getLong("id"), result.getString("name")));
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return authors;
    }

    public List<Book> getAllBooks(){
        String query = "SELECT * FROM books";
        List<Book> books = new LinkedList<>();

        try{
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                books.add(
                        new Book(result.getLong("id"),
                                result.getString("title"),
                                result.getInt("year"),
                                result.getInt("number_of_pages"),
                                new Author(result.getLong("id_author"), ""))
                );
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return books;
    }

    public List<Book> getAllBooksFromAuthor(long authorId){
        String query = "SELECT * FROM books WHERE id_author = " + authorId;
        List<Book> books = new LinkedList<>();

        try{
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                books.add(
                        new Book(result.getLong("id"),
                        result.getString("title"),
                        result.getInt("year"),
                        result.getInt("number_of_pages"),
                        new Author(result.getLong("id_author"), ""))
                );
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return books;
    }

    public void close() throws SQLException {
        statement.close();
        connection.close();
    }
}


// + saving query result into XML

// + search author by ID
// + search book by ISBN

// + add new author
// + add new book for given author

// + remove author (and all his books)
// + remove book (if 0 books for given author, remove author or not? - NOT)

// + change parameters of author and book
// + for author: name
// + for book: title, year, numberOfPages, author

// + return full list of authors
// + return list of books for given author
