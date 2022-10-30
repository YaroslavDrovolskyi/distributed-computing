package ua.edu.yarik;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Library {
    private ArrayList<Author> authors = new ArrayList<>(); // replace by hashmap?
    private ArrayList<Book> books = new ArrayList<>();

//    public saveToFile(); // to be implemented
//    public LoadFromFile(); // to be implemented

    public boolean isAuthorExist(long id){
        for(Author a : authors){
            if (a.getId() == id){
                return true;
            }
        }
        return false;
    }

    public boolean isBookExist(long isbn){
        for(Book b : books){
            if (b.getISBN() == isbn){
                return true;
            }
        }
        return false;
    }

    public Author getAuthorById(long id){
        for(Author a : authors){
            if (a.getId() == id){
                return a;
            }
        }
        throw new NoSuchElementException("No author with given id: " + id);
    }

    public Book getBookByISBN(long isbn){
        for(Book b : books){
            if (b.getISBN() == isbn){
                return b;
            }
        }
        throw new NoSuchElementException("No book with given ISBN: " + isbn);
    }

    public boolean addAuthor(long id, String name){
        if (isAuthorExist(id)){
            return false;
        }
        return authors.add(new Author(id, name));
    }

    public boolean addAuthor(Author author){
        if (isAuthorExist(author.getId())){
            return false;
        }
        return authors.add(author);
    }

    public boolean addBook(long isbn, String title, int year, int pages, long authorId){
        if (isBookExist(isbn)){ // book is already exists
            return false;
        }

        Author author = null;
        try{
            author = getAuthorById(authorId);
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        return books.add(new Book(isbn, title, year, pages, author));
    }

    public boolean addBook(Book book){
        if (isBookExist(book.getISBN())){ // book is already exists
            return false;
        }

        Author author = null;
        try{
            author = getAuthorById(book.getAuthor().getId());
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        return books.add(book);
    }



    public boolean deleteAuthor(long id){
        Author author = null;
        try{
            author = getAuthorById(id);
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        authors.remove(author);

        // remove books wrote by this author
        Iterator<Book> it = books.iterator();
        while(it.hasNext()){
            Book currentBook = it.next();
            if(currentBook.getAuthor() == author){
                it.remove();
            }
        }
        return true;
    }

    // does not remove author if 0 books wrote by him
    public boolean deleteBook(long isbn){
        Book book = null;
        try{
            book = getBookByISBN(isbn);
        } catch(NoSuchElementException e){ // if no such author
            return false;
        }

        books.remove(book);
        return true;
    }

    public void loadFromXml(String filepath){

    }

    public void print(){
        System.out.println("==================== Library ====================");

        System.out.println(">>>>> Authors:");
        if(authors.isEmpty()){
            System.out.println("no authors");
        }
        for(Author a : authors){
            System.out.println(a);
        }

        System.out.println("\n>>>>> Books:");
        if(books.isEmpty()){
            System.out.println("no books");
        }
        for(Book b : books){
            System.out.println(b);
        }

        System.out.println("=================================================");
    }

    public void saveInFile(String filepath){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        // create DOM tree in document
        HashMap<Author, Element> authorElementById = new HashMap<>();
        Document doc = db.newDocument();
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

// change book id to isbn

// loading from XML
// upload into XML

// + search author by ID
// + search book by ISBN

// + add new author
// + add new book for given author

// + remove author (and all him books)
// + remove book (if 0 books for given author, remove author or not?)

// change parameters of author and book

// return full list of authors
// return list of books for given author
