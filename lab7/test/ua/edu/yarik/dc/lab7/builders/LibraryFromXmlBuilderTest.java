package ua.edu.yarik.dc.lab7.builders;

import org.junit.jupiter.api.Test;
import ua.edu.yarik.dc.lab7.Library;

import static org.junit.jupiter.api.Assertions.*;

class LibraryFromXmlBuilderTest {
    Library expectedLib;
    LibraryFromXmlBuilder builder = new LibraryFromXmlBuilder("resources/library-schema.xsd");

    public LibraryFromXmlBuilderTest(){
        expectedLib = new Library();
        // add authors
        expectedLib.addAuthor(1, "author-1");
        expectedLib.addAuthor(2, "author-2");
        expectedLib.addAuthor(3, "author-3");
        expectedLib.addAuthor(4, "author-4");
        expectedLib.addAuthor(5, "author-5");
        expectedLib.addAuthor(6, "author-6");
        expectedLib.addAuthor(7, "author-7");
        expectedLib.addAuthor(10, "author-10");
        expectedLib.addAuthor(9, "author-9");
        expectedLib.addAuthor(8, "author-8");

        // add books
        expectedLib.addBook(1, "book-1", 2020, 976, 1);
        expectedLib.addBook(2, "book-2", 1976, 232, 1);
        expectedLib.addBook(4, "book-3", 2005, 1345, 2);
        expectedLib.addBook(5, "book-4", 1956, 645, 2);
        expectedLib.addBook(6, "book-6", 2006, 65, 3);
        expectedLib.addBook(7, "book-7", 2007, 21, 3);
        expectedLib.addBook(8, "book-8", 2008, 786, 4);
        expectedLib.addBook(9, "book-9", 2009, 12, 4);
        expectedLib.addBook(10, "book-10", 2010, 45, 5);
        expectedLib.addBook(11, "book-11", 2011, 122, 6);
        expectedLib.addBook(12, "book-12", 2012, 67, 7);
        expectedLib.addBook(13, "book-13", 2013, 12, 9);
    }

    @Test
    public void build(){
        // success variants
        Library actualLib = builder.build("resources/library-test-ok.xml");
        assertEquals(expectedLib, actualLib);

        // check not-success variants
        Throwable e1 = assertThrows(RuntimeException.class, () ->{
            builder.build("resources/library-test-fail-1.xml");
        });
        assertEquals("ERROR during parsing XML file", e1.getMessage());

        Throwable e2 = assertThrows(RuntimeException.class, () ->{
            builder.build("resources/library-test-fail-2.xml");
        });
        assertEquals("ERROR during parsing XML file", e2.getMessage());

        Throwable e3 = assertThrows(RuntimeException.class, () ->{
            builder.build("resources/library-test-fail-3.xml");
        });
        assertEquals("ERROR during parsing XML file", e2.getMessage());
    }
}