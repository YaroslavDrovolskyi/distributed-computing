package ua.drovolskyi.dc.exam.sockets.server;

import ua.drovolskyi.dc.exam.customer.Customer;
import ua.drovolskyi.dc.exam.customer.CustomersManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerSocketTask2 {
    private final int PORT;
    private ServerSocket serverSocket;
    private CustomersManager customersManager;

    public ServerSocketTask2(int port, CustomersManager customersManager){
        this.PORT = port;
        this.customersManager = customersManager;
    }

    public void start() throws IOException {
        // create server socket
        serverSocket = new ServerSocket(PORT);

        System.out.println("Server started!");

        // accept new clients, starts handler in new thread for each new client
        while(true){
            Socket socket = serverSocket.accept();
            new Thread(new ClientHandlerThread(socket, customersManager)).start();
        }
    }

    public static class ClientHandlerThread implements Runnable{
        private final Socket clientSocket;
        private final CustomersManager customersManager;
        private ObjectInputStream inputStream = null;
        private ObjectOutputStream outputStream = null;

        public ClientHandlerThread(Socket clientSocket, CustomersManager customersManager){
            this.clientSocket = clientSocket;
            this.customersManager = customersManager;
        }

        @Override
        public void run(){
            // get client's streams
            System.out.println("New client connected: " + clientSocket);
            try{
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.flush();
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
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
                            addCustomer();
                            break;
                        case 2:
                            getCustomersInAlphabetOrder();
                            break;
                        case 3:
                            selectInCardInterval();
                            break;
                        default:
                            // write about unknown command
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

        private void addCustomer() throws IOException, ClassNotFoundException {
            Customer c = (Customer)inputStream.readObject();

            boolean result = customersManager.addCustomer(c);

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
            outputStream.flush();
        }

        private void getCustomersInAlphabetOrder() throws IOException, ClassNotFoundException {
            List<Customer> result = customersManager.getCustomersInAlphabetOrder();

            outputStream.writeInt(1);
            outputStream.writeObject(result);
            outputStream.flush();
        }

        private void selectInCardInterval() throws IOException, ClassNotFoundException {
            long start = inputStream.readLong();
            long end = inputStream.readLong();

            try{
                List<Customer> result = customersManager.selectInCardInterval(start, end);

                outputStream.writeInt(1);
                outputStream.writeObject(result);
            } catch(Exception e){
                outputStream.writeInt(-2); // write about error
            } finally {
                outputStream.flush();
            }
        }
    }


    }
