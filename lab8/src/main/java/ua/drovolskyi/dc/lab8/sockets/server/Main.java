package ua.drovolskyi.dc.lab8.sockets.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Main {
    private static final int PORT = 50000;


    public static void main(String[] args) throws SQLException, IOException {
        Server server = new Server(50000);
        server.start();


    }
}
