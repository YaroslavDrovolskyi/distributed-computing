package ua.drovolskyi.dc.lab8.sockets.server;

import ua.drovolskyi.dc.lab8.library.LibraryDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
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

}
