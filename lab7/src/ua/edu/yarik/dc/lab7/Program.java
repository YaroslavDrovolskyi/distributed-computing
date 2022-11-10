package ua.edu.yarik.dc.lab7;

import ua.edu.yarik.dc.lab7.builders.LibraryFromXmlBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Program {
    public static void main(String args[]){
        /*
        File f = new File("resources/library-test-ok.xml");
        LibraryFromXmlBuilder builder = new LibraryFromXmlBuilder(f.getPath(), "resources/library-schema.xsd");

        Library library = builder.build();
        library.print();

        library.saveInFile("resources/library-output.xml");

        demo1();
        */

        LibraryDB db = null;
        try {
            db = new LibraryDB();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (SQLException e) {
            System.out.println("SQL error occurred: " + e.getMessage());
        }

        System.out.println("Truncate tables: " + db.deleteAllAuthors() + ", " + db.deleteAllBooks());

        System.out.println("Add authors");
        System.out.println(db.addAuthor(250, "Author-3"));
        System.out.println(db.addAuthor(251, "Author-4"));
        System.out.println(db.addAuthor(252, "Author-5"));
        System.out.println(db.addAuthor(253, "Author-6"));
        System.out.println(db.addAuthor(254, "Author-7"));
        System.out.println("Add existing author: " + db.addAuthor(250, "Author-7"));

        System.out.println("Add books");
        System.out.println(db.addBook(1000, "Book-1", 2005, 100, 250));
        System.out.println(db.addBook(1001, "Book-2", 2005, 100, 251));
        System.out.println(db.addBook(1002, "Book-3", 2005, 100, 252));
        System.out.println("Add exist book: " + db.addBook(1000, "Book-4", 2005, 100, 250));
        System.out.println("Add book of non-existent author: "+ db.addBook(1004, "Book-5", 2005, 100, 1000));


        try {
            db.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


//        demo1();
    }

    private static void demo1(){
        System.out.println("Add authors:");
        Library library = new Library();
        for(int i = 1; i <= 10; i++){
            System.out.println(
                    library.addAuthor(1000 + i, "Author-" + i)
            );

        }

        System.out.println("Add books:");
        for(int i = 1; i <= 10; i++){
            for(int j = 1; j <= 5; j++){
                System.out.println(
                        library.addBook(1050 + i + 50*j, "Title-" + i + "-" + j, 2000, i*j, 1000+i)
                );
            }
        }

        library.print();

        // add items, with already exists ids
        System.out.println("\nAdd items, with already exists ids");
        System.out.println(
                library.addAuthor(1001, "Title-")
        );
        System.out.println(
                library.addBook(1101, "Title-", 2000, 100, 1000+1)
        );

        // remove correct items
        System.out.println("\nRemove existent items");
        System.out.println(
                library.deleteAuthor(1001)
        );
        System.out.println(
                library.deleteBook(1257)
        );

        // remove non-exist items
        System.out.println("\nRemove non-exist items");
        System.out.println(
                library.deleteAuthor(1000)
        );
        System.out.println(
                library.deleteBook(1500)
        );

        library.print();

        // searching
        System.out.println("\nSearch for existent items:");
        if(library.isAuthorExist(1010)){
            System.out.println(library.getAuthorById(1010));
        }
        else{
            System.out.println("Author does not exist");
        }
        if(library.isBookExist(1102)){
            System.out.println(library.getBookByISBN(1102));
        }
        else{
            System.out.println("Book does not exist");
        }

        System.out.println("\nSearch for non-existent items:");
        if(library.isAuthorExist(10000)){
            System.out.println(library.getAuthorById(10000));
        }
        else{
            System.out.println("Author does not exist");
        }
        if(library.isBookExist(10000)){
            System.out.println(library.getBookByISBN(10000));
        }
        else{
            System.out.println("Book does not exist");
        }

        LibraryWriterXML.saveLibraryInFile(library, "resources/demo1-output.xml");

        LibraryFromXmlBuilder builder1 = new LibraryFromXmlBuilder("resources/demo1-output.xml");
        Library libr = builder1.build("resources/library-schema.xsd");
        libr.print();
    }
}
