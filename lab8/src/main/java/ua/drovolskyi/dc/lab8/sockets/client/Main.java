package ua.drovolskyi.dc.lab8.sockets.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client("localhost", 50000);
        client.addAuthor(100001, "Name-1");
        System.out.println(client.getAllAuthors());
 //       client.addAuthor(1000, "Name");
//        System.out.println(client.getAllAuthors());
    }
}
