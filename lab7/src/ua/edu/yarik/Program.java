package ua.edu.yarik;

import ua.edu.yarik.builders.LibraryFromXmlBuilder;

import java.io.File;

public class Program {
    public static void main(String args[]){
        File f = new File("resources/library.xml");
        LibraryFromXmlBuilder builder = new LibraryFromXmlBuilder(f.getPath(), "resources/library-schema.xsd");

        Library library = builder.build();
        library.print();

        library.saveInFile("resources/library-output.xml");

        demo1();




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

        library.saveInFile("resources/demo1-output.xml");

        LibraryFromXmlBuilder builder1 = new LibraryFromXmlBuilder("resources/demo1-output.xml", "resources/library-schema.xsd");
        Library libr = builder1.build();
        libr.print();
    }
}
