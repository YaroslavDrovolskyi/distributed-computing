package ua.drovolskyi.dc.lab8.rabbitmq.client;

import ua.drovolskyi.dc.lab8.library.Author;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args)
            throws IOException, TimeoutException, ExecutionException, InterruptedException, ClassNotFoundException {
        Client client = new Client();

        for(int i = 0 ; i < 20; i++){
            client.addBook(i, "book-"+i, 2000+i, 100+i, new Author(i/2, ""));
        }

        System.out.println("Add author");
        System.out.println("Result = " + client.addAuthor(101, "Name-2"));

        System.out.println("Delete existent author");
        System.out.println("Result = " + client.deleteAuthor(101));

        System.out.println("Delete non-existent author");
        System.out.println("Result = " + client.deleteAuthor(101));

        System.out.println("\nAdd book to existent author");
        System.out.println("Result = " +
                client.addBook(12343, "Title-1", 2006, 100, new Author(5, "")));

        System.out.println("Add book to non-existent author");
        System.out.println("Result = " +
                client.addBook(456324, "Title-1", 2006, 100, new Author(10, "")));

        System.out.println("\nDelete existent book");
        System.out.println("Result = " + client.deleteBook(12343));
        System.out.println("Delete non-existent book");
        System.out.println("Result = " + client.deleteBook(12343));

        // change book author
        System.out.print("\nChange existent book author to existent author: ");
        System.out.println(client.changeBookAuthor(0, 7));

        System.out.print("Change existent book author to non-existent author: ");
        System.out.println(client.changeBookAuthor(0, 10));

        System.out.print("Change non-existent book author to existent author: ");
        System.out.println(client.changeBookAuthor(101, 8));

        System.out.print("Change non-existent book author to non-existent author: ");
        System.out.println(client.changeBookAuthor(101, 10));

        // change year
        System.out.print("\nChange year of existent book: ");
        System.out.println(client.changeBookYear(0, 2001));

        System.out.print("Change year of non-existent book: ");
        System.out.println(client.changeBookYear(101, 2001));

        // change number of pages
        System.out.print("\nChange number_of_pages of existent book: ");
        System.out.println(client.changeBookNumberOfPages(0, 100));

        System.out.print("Change number_of_pages of non-existent book: ");
        System.out.println(client.changeBookNumberOfPages(101, 325));

        // change title
        System.out.print("\nChange title of existent book: ");
        System.out.println(client.changeBookTitle(0, "newTitle-0"));

        System.out.print("Change title of non-existent book: ");
        System.out.println(client.changeBookTitle(101, "newTitle-0"));

        // count books
        System.out.print("\nCount books: ");
        System.out.println(client.getNumberOfBooks());

        // get all books
        System.out.println("\nGet all books: ");
        System.out.println(client.getAllBooks());

        // get all books from author
        System.out.println("\nGet all books from existent author: ");
        System.out.println(client.getAllBooksFromAuthor(5));

        System.out.println("Get all books from non-existent author: ");
        System.out.println(client.getAllBooksFromAuthor(11));

        // get all authors
        System.out.println("\nGet all authors: ");
        System.out.println(client.getAllAuthors());

        client.close();
    }
}
