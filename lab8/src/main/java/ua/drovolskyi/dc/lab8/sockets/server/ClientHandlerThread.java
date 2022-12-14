package ua.drovolskyi.dc.lab8.sockets.server;

import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.LibraryDB;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;

public class ClientHandlerThread implements Runnable{
    private Socket clientSocket;
    private  LibraryDB db;
    private ReadWriteLock dbLock;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;

    public ClientHandlerThread(Socket clientSocket, LibraryDB db, ReadWriteLock dbLock){
        this.clientSocket = clientSocket;
        this.db = db;
        this.dbLock = dbLock;
    }

    @Override
    public void run(){
        try{
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // process client queries
        while(true){
            try {
                int command = inputStream.readInt();
                switch(command){
                    case 1:
                        addAuthor();
                        break;
                    case 2:
                        // deleteAuthor();
                        break;
                    case 3:
                        // addBook();
                        break;
                    case 4:
                        // deleteBook();
                        break;
                    case 5:
                        // editBook();
                        break;
                    case 6:
                        // countBooks();
                        break;
                    case 7:
                        // getAllBooks();
                        break;
                    case 8:
                        // getAllBooksFromAuthor();
                        break;
                    case 9:
                        // getAllAuthors();
                        break;
                    default:
                        // write about error
                        break;
                }
            } catch (IOException e) {
                return;
            }
        }
    }

    // there will be methods

    void addAuthor() throws IOException, ClassNotFoundException {
        Author a = (Author)inputStream.readObject();
    }

}

/*
    1. Додавання нового автора
    2. Видалення автора
    3. Додавання нової книги для автора
    4. Видалення книги
    5. Редагування книги
    6. Підрахунок загальної кількості книг
    7. Отримання повного списку книг із зазначенням ПІБ автора
    8. Отримання книг заданого автора
    9. Отримання списку авторів
 */
