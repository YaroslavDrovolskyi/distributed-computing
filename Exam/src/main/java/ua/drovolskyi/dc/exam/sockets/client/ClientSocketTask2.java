package ua.drovolskyi.dc.exam.sockets.client;

import ua.drovolskyi.dc.exam.customer.Customer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ClientSocketTask2 {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ClientSocketTask2(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connected to server");
    }

    public boolean addCustomer(Customer c) throws IOException {
        outputStream.writeInt(1);
        outputStream.writeObject(c);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Customer> getCustomersInAlphabetOrder() throws IOException, ClassNotFoundException {
        outputStream.writeInt(2);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List<Customer>) inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Customer> selectInCardInterval(long start, long end) throws IOException, ClassNotFoundException {
        outputStream.writeInt(3);
        outputStream.writeLong(start);
        outputStream.writeLong(end);
        outputStream.flush();

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List<Customer>) inputStream.readObject();
        }
        else if (returnCode == -2){
            throw new IllegalArgumentException();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public void disconnect() throws IOException {
        socket.close();
    }
}
