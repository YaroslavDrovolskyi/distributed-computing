package ua.drovolskyi.dc.exam.sockets.server;

import ua.drovolskyi.dc.exam.customer.CustomersManager;

import java.io.IOException;

public class Main {
    private static final int PORT = 50000;

    public static void main(String[] args) throws IOException {
        CustomersManager customersManager = new CustomersManager(10);
        
        ServerSocketTask2 server = new ServerSocketTask2(PORT, customersManager);
        server.start();
    }
}
