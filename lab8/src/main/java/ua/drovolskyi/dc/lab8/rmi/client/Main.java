package ua.drovolskyi.dc.lab8.rmi.client;

import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.rmi.interfaces.LibraryRMI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args)
            throws IOException, NotBoundException, ClassNotFoundException {
        LibraryRMI lib = (LibraryRMI) Naming.lookup("//localhost:1099/Library");
        System.out.println("RMI object found");

        for(int i = 0 ; i < 20; i++){
            lib.addBook(i, "book-"+i, 2000+i, 100+i,i/2);
        }

        System.out.println("Add author");
        System.out.println("Result = " + lib.addAuthor(101, "Name-2"));

        System.out.println("Delete existent author");
        System.out.println("Result = " + lib.deleteAuthor(101));

        System.out.println("Delete non-existent author");
        System.out.println("Result = " + lib.deleteAuthor(101));

        System.out.println("\nAdd book to existent author");
        System.out.println("Result = " +
                lib.addBook(12343, "Title-1", 2006, 100, 5));

        System.out.println("Add book to non-existent author");
        System.out.println("Result = " +
                lib.addBook(456324, "Title-1", 2006, 100, 10));

        System.out.println("\nDelete existent book");
        System.out.println("Result = " + lib.deleteBook(12343));
        System.out.println("Delete non-existent book");
        System.out.println("Result = " + lib.deleteBook(12343));

        // change book author
        System.out.print("\nChange existent book author to existent author: ");
        System.out.println(lib.changeBookAuthor(0, 7));

        System.out.print("Change existent book author to non-existent author: ");
        System.out.println(lib.changeBookAuthor(0, 10));

        System.out.print("Change non-existent book author to existent author: ");
        System.out.println(lib.changeBookAuthor(101, 8));

        System.out.print("Change non-existent book author to non-existent author: ");
        System.out.println(lib.changeBookAuthor(101, 10));

        // change year
        System.out.print("\nChange year of existent book: ");
        System.out.println(lib.changeBookYear(0, 2001));

        System.out.print("Change year of non-existent book: ");
        System.out.println(lib.changeBookYear(101, 2001));

        // change number of pages
        System.out.print("\nChange number_of_pages of existent book: ");
        System.out.println(lib.changeBookNumberOfPages(0, 100));

        System.out.print("Change number_of_pages of non-existent book: ");
        System.out.println(lib.changeBookNumberOfPages(101, 325));

        // change title
        System.out.print("\nChange title of existent book: ");
        System.out.println(lib.changeBookTitle(0, "newTitle-0"));

        System.out.print("Change title of non-existent book: ");
        System.out.println(lib.changeBookTitle(101, "newTitle-0"));

        // count books
        System.out.print("\nCount books: ");
        System.out.println(lib.countBooks());

        // get all books
        System.out.println("\nGet all books: ");
        System.out.println(lib.getAllBooks());

        // get all books from author
        System.out.println("\nGet all books from existent author: ");
        System.out.println(lib.getAllBooksFromAuthor(5));

        System.out.println("Get all books from non-existent author: ");
        System.out.println(lib.getAllBooksFromAuthor(11));

        // get all authors
        System.out.println("\nGet all authors: ");
        System.out.println(lib.getAllAuthors());

    }
}
