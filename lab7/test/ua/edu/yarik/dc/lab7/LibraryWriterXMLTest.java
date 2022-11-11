package ua.edu.yarik.dc.lab7;

import org.junit.jupiter.api.Test;
import ua.edu.yarik.dc.lab7.builders.LibraryFromXmlBuilder;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LibraryWriterXMLTest {
    LibraryFromXmlBuilder builder;
    LibraryDB library;

    public LibraryWriterXMLTest(){
        builder = new LibraryFromXmlBuilder("resources/library-schema.xsd");

        // fill DB
        try {
            library = new LibraryDB();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // add authors
        for(int i = 0; i < 10; i++){
            library.addAuthor(i, "author-" + i);
        }
        // add books
        for(int i = 0; i < 18; i++){
            library.addBook(i, "book-"+i, 2000+i, 100+i, i/2);
        }
    }

    @Test
    void saveLibraryInFile() {
        // create expected lib
        Library expectedLib = new Library();
        expectedLib = builder.build("resources/library-test-ok.xml");

        // write in file
        LibraryWriterXML.saveLibraryInFile(expectedLib, "resources/library-test-writing.xml");

        // read from file
        Library actualLib = builder.build("resources/library-test-writing.xml");

        assertEquals(expectedLib, actualLib);
    }

    @Test
    void saveAuthorsInFile(){
        LibraryWriterXML.saveAuthorsInFile(library.getAllAuthors(), "resources/authors-from-db-test.xml");
    }
    @Test
    void saveBooksInFile(){
        LibraryWriterXML.saveBooksInFile(library.getAllBooks(), "resources/books-from-db-test.xml");
    }
}