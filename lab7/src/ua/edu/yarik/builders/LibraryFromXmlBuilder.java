package ua.edu.yarik.builders;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ua.edu.yarik.Author;
import ua.edu.yarik.Book;
import ua.edu.yarik.Library;

import javax.print.Doc;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class LibraryFromXmlBuilder {
    String xmlPath;
    String xsdPath;
    DocumentBuilder docBuilder;

    public LibraryFromXmlBuilder(String xmlPath, String xsdPath){
        this.xmlPath = xmlPath;
        this.xsdPath = xsdPath;

        try {
            // create schema
            Schema schema = null;
            if(xsdPath != ""){
                String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
                SchemaFactory schemaFactory = SchemaFactory.newInstance(language);
                schema = schemaFactory.newSchema(new File(xsdPath));
            }


            // create document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            if(xsdPath != ""){
                dbf.setSchema(schema);
            }
            docBuilder = dbf.newDocumentBuilder();
            docBuilder.setErrorHandler(new StudentsParsingErrorHandler());
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public Library build(){
        Library library = new Library();
        Document doc = null;
        try {
            doc = docBuilder.parse(new File(xmlPath));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Element root = doc.getDocumentElement();
        NodeList authorsElements = root.getElementsByTagName("Author");
        for(int i = 0; i < authorsElements.getLength(); i++){
            Element authorElement = (Element) authorsElements.item(i);
            Author author = buildAuthor(authorElement);
            library.addAuthor(author); // add author

            NodeList authorBooksElements = authorElement.getElementsByTagName("Book");
            for(int j = 0; j < authorBooksElements.getLength(); j++){
                Element bookElement = (Element) authorBooksElements.item(j);
                Book book = buildBook(author, bookElement);
                library.addBook(book); // add book
            }
        }

        return library;
    }

    private Author buildAuthor(Element element){
        long id = Integer.parseInt(element.getAttribute("id").substring(3));
        String name = element.getAttribute("name");

        return new Author(id, name);
    }

    private Book buildBook(Author author, Element element){
        long isbn = Integer.parseInt(element.getAttribute("id").substring(5));
        String title = element.getAttribute("title");
        int year = Integer.parseInt(element.getAttribute("year"));
        int numberOfPages = Integer.parseInt(element.getAttribute("numberOfPages"));
        return new Book(isbn, title, year, numberOfPages, author);
    }


}


class StudentsParsingErrorHandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.out.println("Warning: line " + e.getLineNumber() +
                ", col " + e.getColumnNumber() + " '" + e.getMessage() + "'");
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        System.out.println("Error: line " + e.getLineNumber() +
                ", col " + e.getColumnNumber() + " '" + e.getMessage() + "'");
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println("Fatal error: line " + e.getLineNumber() +
                ", col " + e.getColumnNumber() + " '" + e.getMessage() + "'");
    }
}

// page 426 - XSD validation
// page 449 - DOM