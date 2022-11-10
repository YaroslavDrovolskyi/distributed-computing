package ua.edu.yarik.dc.lab7;

import org.junit.jupiter.api.Test;
import ua.edu.yarik.dc.lab7.builders.LibraryFromXmlBuilder;

import static org.junit.jupiter.api.Assertions.*;

class LibraryWriterXMLTest {
    LibraryFromXmlBuilder builder;

    public LibraryWriterXMLTest(){
        builder = new LibraryFromXmlBuilder("resources/library-schema.xsd");
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
}