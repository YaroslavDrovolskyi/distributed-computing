package ua.drovolskyi.dc.lab8.sockets.server;

import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;
import ua.drovolskyi.dc.lab8.library.LibraryDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private final int PORT;
    private ServerSocket serverSocket;
    private LibraryDB db;
    private ReadWriteLock dbLock;

    public Server(int port) throws SQLException, FileNotFoundException {
        this.PORT = port;
        db = new LibraryDB();
        dbLock = new ReentrantReadWriteLock();
    }

    public void start() throws IOException {
        // create server socket
        serverSocket = new ServerSocket(PORT);

        // accept new clients, starts handler in new thread for each new client
        while(true){
            Socket socket = serverSocket.accept();
            new Thread(new ClientHandlerThread(socket, db, dbLock)).start();
        }
    }

    public static class ClientHandlerThread implements Runnable{
        private final Socket clientSocket;
        private final LibraryDB db;
        private final ReadWriteLock dbLock;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;

        public ClientHandlerThread(Socket clientSocket, LibraryDB db, ReadWriteLock dbLock){
            this.clientSocket = clientSocket;
            this.db = db;
            this.dbLock = dbLock;
        }

        @Override
        public void run(){
            System.out.println("New client connected: " + clientSocket);
            try{
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.flush();
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                System.out.println("get input stream");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // process client queries
            while(true){
                try {
                    System.out.println("Waiting for command from client...");
                    int command = inputStream.readInt();
                    System.out.println("Got new command: " + command + " from client " + clientSocket);
                    switch(command){
                        case 1:
                            addAuthor();
                            break;
                        case 2:
                            deleteAuthor();
                            break;
                        case 3:
                            addBook();
                            break;
                        case 4:
                            deleteBook();
                            break;
                        case 5:
                            editBook();
                            break;
                        case 6:
                            countBooks();
                            break;
                        case 7:
                            getAllBooks();
                            break;
                        case 8:
                            getAllBooksFromAuthor();
                            break;
                        case 9:
                            getAllAuthors();
                            break;
                        default:
                            // write about error
                            outputStream.writeInt(-1);
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Client " + clientSocket + " disconnected");
                    return;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Command executed");
            }
        }

        void addAuthor() throws IOException, ClassNotFoundException {
            Author a = (Author)inputStream.readObject();

            dbLock.writeLock().lock();
            boolean result = db.addAuthor(a.getId(), a.getName());
            dbLock.writeLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
            outputStream.flush();
        }

        void deleteAuthor() throws IOException {
            long id = inputStream.readLong();
            try{
                dbLock.writeLock().lock();
                boolean result = db.deleteAuthor(id);
                dbLock.writeLock().unlock();

                outputStream.writeInt(1);
                outputStream.writeBoolean(result);
            } catch(RuntimeException e){
                outputStream.writeInt(-1);
            } finally{
                outputStream.flush();
            }
        }

        void addBook() throws IOException, ClassNotFoundException {
            Book b = (Book)inputStream.readObject();

            dbLock.writeLock().lock();
            boolean result = db.addBook(b.getISBN(), b.getTitle(), b.getYear(), b.getNumberPages(), b.getAuthor().getId());
            dbLock.writeLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
            outputStream.flush();
        }

        void deleteBook() throws IOException {
            long isbn = inputStream.readLong();

            dbLock.writeLock().lock();
            boolean result = db.deleteBook(isbn);
            dbLock.writeLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
            outputStream.flush();
        }

        void editBook() throws IOException {
            int operationCode = inputStream.readInt();
            long isbn = inputStream.readLong();
            boolean result;

            dbLock.writeLock().lock();
            switch (operationCode){
                case 1: // change title
                    String newTitle = inputStream.readUTF();
                    result = db.changeBookTitle(isbn, newTitle);
                    break;
                case 2: // change year
                    int newYear = inputStream.readInt();
                    result = db.changeBookYear(isbn, newYear);
                    break;
                case 3: // change number of pages
                    int newNumberOfPages = inputStream.readInt();
                    result = db.changeBookNumberOfPages(isbn, newNumberOfPages);
                    break;
                case 4: // change author id
                    long newId = inputStream.readLong();
                    result = db.changeBookAuthor(isbn, newId);
                    break;
                default:
                    outputStream.writeInt(-1);
                    dbLock.writeLock().unlock();
                    return;
            }
            dbLock.writeLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
            outputStream.flush();
        }

        void countBooks() throws IOException {
            dbLock.readLock().lock();
            int numberOfBooks = db.getNumberOfBooks();
            dbLock.readLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeInt(numberOfBooks);
            outputStream.flush();
        }

        void getAllBooks() throws IOException {
            dbLock.readLock().lock();
            List<Book> books = db.getAllBooks();
            dbLock.readLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeObject(books);
            outputStream.flush();
        }

        void getAllBooksFromAuthor() throws IOException {
            long id = inputStream.readLong();

            dbLock.readLock().lock();
            List<Book> books = db.getAllBooksFromAuthor(id);
            dbLock.readLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeObject(books);
            outputStream.flush();
        }

        void getAllAuthors() throws IOException {
            dbLock.readLock().lock();
            List<Author> authors = db.getAllAuthors();
            dbLock.readLock().unlock();

            outputStream.writeInt(1);
            outputStream.writeObject(authors);
            outputStream.flush();
        }


    }
}
