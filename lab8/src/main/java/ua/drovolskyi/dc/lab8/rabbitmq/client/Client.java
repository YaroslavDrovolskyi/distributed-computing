package ua.drovolskyi.dc.lab8.rabbitmq.client;

import com.rabbitmq.client.*;
import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Client implements AutoCloseable{
    private Connection connection;
    private Channel channel;
    private final String responseQueueName; // server will have one response queue per client
    private final String REQUEST_QUEUE_NAME = "library_tasks_queue";

    public Client() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        // declare queue for requests
        channel.queueDeclare(REQUEST_QUEUE_NAME, true, false, false,null);

        // declare queue for response
        responseQueueName = "response_queue_" + UUID.randomUUID().toString();
        channel.queueDeclare(responseQueueName, true, false, false,null);
    }

    private byte[] sendRequestGetResponse(byte[] message) throws IOException, ExecutionException, InterruptedException {
        String correlationId = UUID.randomUUID().toString();

        // set properties of request message
        AMQP.BasicProperties requestProperties = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(responseQueueName)
                .build();

        // send request
        channel.basicPublish("", REQUEST_QUEUE_NAME, requestProperties, message);


        CompletableFuture<byte[]> response = new CompletableFuture<>();

        // create consumer that will listen to response queue
        String consumerTag = channel.basicConsume(responseQueueName,
                false, // autoAck
                new DeliverCallback() {
                    @Override
                    public void handle(String s, Delivery delivery) throws IOException {
                        if(delivery.getProperties().getCorrelationId().equals(correlationId)){
                            response.complete(delivery.getBody());
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
                    }
                },
                new CancelCallback() {
                    @Override
                    public void handle(String s) throws IOException {

                    }
                });

        byte[] result = response.get(); // wait there until response come

        channel.basicCancel(consumerTag); // cancel consumer, because we received response

        return result;
    }

    public boolean addAuthor(long id, String name)
            throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(1);
        outputStream.writeObject(new Author(id, name));
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();
//        System.out.println("Command executed, return code = " + returnCode);

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean deleteAuthor(long id) throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(2);
        outputStream.writeLong(id);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean addBook(long isbn, String title, int year, int pages, Author author)
            throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(3);
        outputStream.writeObject(new Book(isbn, title, year, pages, author));
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean deleteBook(long isbn) throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(4);
        outputStream.writeLong(isbn);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookTitle(long isbn, String title) throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(5);
        outputStream.writeInt(1);
        outputStream.writeLong(isbn);
        outputStream.writeUTF(title);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookYear(long isbn, int year) throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(5);
        outputStream.writeInt(2);
        outputStream.writeLong(isbn);
        outputStream.writeInt(year);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookNumberOfPages(long isbn, int pages) throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(5);
        outputStream.writeInt(3);
        outputStream.writeLong(isbn);
        outputStream.writeInt(pages);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public boolean changeBookAuthor(long isbn, long authorId) throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(5);
        outputStream.writeInt(4);
        outputStream.writeLong(isbn);
        outputStream.writeLong(authorId);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readBoolean();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public int getNumberOfBooks() throws IOException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(6);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return inputStream.readInt();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Book> getAllBooks() throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(7);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List<Book>)inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Book> getAllBooksFromAuthor(long authorId)
            throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(8);
        outputStream.writeLong(authorId);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List<Book>)inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    public List<Author> getAllAuthors()
            throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        // write arguments in byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeInt(9);
        outputStream.flush();

        byte[] response = sendRequestGetResponse(byteArrayOutputStream.toByteArray());

        // read result from response byte array
        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(response));

        int returnCode = inputStream.readInt();

        if(returnCode == 1){
            return (List)inputStream.readObject();
        }
        else{
            throw new RuntimeException("Unknown command");
        }
    }

    @Override
    public void close() throws IOException {
        channel.queueDelete(responseQueueName); // delete queue for responses
        connection.close();
    }
}
