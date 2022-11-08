package ua.edu.yarik.dc.lab7;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class LibraryWriterXML {
    public static void saveAuthorsInFile(List<Author> authors, String filepath){
        Document doc = createNewDocument();

        // create DOM tree
        Element root = doc.createElement("Authors");
        doc.appendChild(root);
        for(Author author : authors){
            Element authorDOM = doc.createElement("Author");
            authorDOM.setAttribute("id", "id-" + String.valueOf(author.getId()));
            authorDOM.setAttribute("name", author.getName());
            root.appendChild(authorDOM);
        }

        writeDocumentInFile(doc, filepath);
    }

    public static void saveBooksInFile(List<Book> books, String filepath){
        Document doc = createNewDocument();

        // create DOM tree
        Element root = doc.createElement("Books");
        doc.appendChild(root);
        for(Book book : books){
            Element bookDOM = doc.createElement("Book");
            bookDOM.setAttribute("id", "isbn-" + String.valueOf(book.getISBN()));
            bookDOM.setAttribute("title", book.getTitle());
            bookDOM.setAttribute("year", String.valueOf(book.getYear()));
            bookDOM.setAttribute("numberOfPages", String.valueOf(book.getNumberPages()));
            bookDOM.setAttribute("id_author", String.valueOf(book.getAuthor().getId()));
            root.appendChild(bookDOM);
        }

        writeDocumentInFile(doc, filepath);
    }

    public static void saveLibraryInFile(Library library, String filepath){
        Document doc = createNewDocument();

        List<Author> authors = library.getAllAuthors();
        List<Book> books = library.getAllBooks();

        // create DOM tree in document
        HashMap<Author, Element> authorElementById = new HashMap<>();
        Element root = doc.createElement("Library");
        doc.appendChild(root);
        for(Author author : authors){
            Element authorElement = doc.createElement("Author");
            authorElement.setAttribute("id", "id-" + String.valueOf(author.getId()));
            authorElement.setAttribute("name", author.getName());
            root.appendChild(authorElement);
            authorElementById.put(author, authorElement);
        }

        for(Book book : books){
            Element bookElement = doc.createElement("Book");
            bookElement.setAttribute("id", "isbn-" + String.valueOf(book.getISBN()));
            bookElement.setAttribute("title", book.getTitle());
            bookElement.setAttribute("year", String.valueOf(book.getYear()));
            bookElement.setAttribute("numberOfPages", String.valueOf(book.getNumberPages()));
            Element authorElement = authorElementById.get(book.getAuthor());
            authorElement.appendChild(bookElement);
        }

        // write DOM tree in file
        writeDocumentInFile(doc, filepath);
    }

    private static Document createNewDocument(){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc = db.newDocument();
        return doc;
    }

    private static void writeDocumentInFile(Document doc, String filepath){
        // write DOM tree in file
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult stream = new StreamResult(new FileWriter(filepath));
            transformer.transform(source, stream);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
