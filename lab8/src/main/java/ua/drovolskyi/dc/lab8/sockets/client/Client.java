package ua.drovolskyi.dc.lab8.sockets.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public void disconnect() throws IOException {
        socket.close();
    }
}
