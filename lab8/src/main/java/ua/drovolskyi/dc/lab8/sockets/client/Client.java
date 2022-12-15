package ua.drovolskyi.dc.lab8.sockets.client;

import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Client {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connected to server");
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public boolean addAuthor(long id, String name) throws IOException {
        outputStream.writeInt(1);
        outputStream.writeObject(new Author(id, name));
        outputStream.flush();

        int returnCode = inputStream.readInt();
        System.out.println("Command executed, return code = " + returnCode);

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean deleteAuthor(long id) throws IOException {
        outputStream.writeInt(2);
        outputStream.writeLong(id);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean addBook(long isbn, String title, int year, int pages, Author author) throws IOException {
        outputStream.writeInt(3);
        outputStream.writeObject(new Book(isbn, title, year, pages, author));
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean deleteBook(long isbn) throws IOException {
        outputStream.writeInt(4);
        outputStream.writeLong(isbn);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookTitle(long isbn, String title) throws IOException {
        outputStream.writeInt(5);
        outputStream.writeInt(1);
        outputStream.writeLong(isbn);
        outputStream.writeObject(title);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookYear(long isbn, int year) throws IOException {
        outputStream.writeInt(5);
        outputStream.writeInt(2);
        outputStream.writeLong(isbn);
        outputStream.writeInt(year);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookNumberOfPages(long isbn, int pages) throws IOException {
        outputStream.writeInt(5);
        outputStream.writeInt(3);
        outputStream.writeLong(isbn);
        outputStream.writeInt(pages);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookAuthor(long isbn, long authorId) throws IOException {
        outputStream.writeInt(5);
        outputStream.writeInt(4);
        outputStream.writeLong(isbn);
        outputStream.writeLong(authorId);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public int getNumberOfBooks() throws IOException {
        outputStream.writeInt(6);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readInt();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Book> getAllBooks() throws IOException, ClassNotFoundException {
        outputStream.writeInt(7);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List<Book>)inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Book> getAllBooksFromAuthor(long authorId) throws IOException, ClassNotFoundException {
        outputStream.writeInt(8);
        outputStream.writeLong(authorId);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List<Book>)inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Author> getAllAuthors() throws IOException, ClassNotFoundException {
        outputStream.writeInt(9);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List)inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }
}
