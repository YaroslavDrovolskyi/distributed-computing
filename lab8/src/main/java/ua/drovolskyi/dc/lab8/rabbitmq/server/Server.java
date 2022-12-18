package ua.drovolskyi.dc.lab8.rabbitmq.server;

import com.rabbitmq.client.*;
import ua.drovolskyi.dc.lab8.library.Author;
import ua.drovolskyi.dc.lab8.library.Book;
import ua.drovolskyi.dc.lab8.library.LibraryDB;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Server {
    private final LibraryDB db;
    private final String QUEUE_NAME = "library_tasks_queue";
    private final Connection connection;
    private final Channel channel;

    public Server() throws IOException, TimeoutException, SQLException {
        db = new LibraryDB();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false,null);
        channel.queuePurge(QUEUE_NAME);

        channel.basicQos(1);

        // create request handler
        DeliverCallback deliverCallback = new RequestHandler(db, channel);

        // start consumer, that will consume messages
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));

        System.out.println("Server started!");
    }



    // Object of this class will process client requests
    public static class RequestHandler implements DeliverCallback {
        private LibraryDB db;
        private Channel channel;

        public RequestHandler(LibraryDB db, Channel channel) {
            this.db = db;
            this.channel = channel;
        }

        @Override
        public void handle(String s, Delivery delivery) throws IOException {
            AMQP.BasicProperties replyProperties = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();


            // create input stream
            ObjectInputStream inputStream = new ObjectInputStream(
                    new ByteArrayInputStream(delivery.getBody()));

            // create output stream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

            // write response in output stream
            int command = inputStream.readInt();
//            System.out.println("Got new command: " + command + " from client " + clientSocket);
            try {
                switch (command) {
                    case 1:
                        addAuthor(inputStream, outputStream);
                        break;
                    case 2:
                        deleteAuthor(inputStream, outputStream);
                        break;
                    case 3:
                        addBook(inputStream, outputStream);
                        break;
                    case 4:
                        deleteBook(inputStream, outputStream);
                        break;
                    case 5:
                        editBook(inputStream, outputStream);
                        break;
                    case 6:
                        countBooks(outputStream);
                        break;
                    case 7:
                        getAllBooks(outputStream);
                        break;
                    case 8:
                        getAllBooksFromAuthor(inputStream, outputStream);
                        break;
                    case 9:
                        getAllAuthors(outputStream);
                        break;
                    default:
                        // write about unknown command
                        outputStream.writeInt(-1);
                        break;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally{ // send a response
                channel.basicPublish("", delivery.getProperties().getReplyTo(),
                        replyProperties, byteArrayOutputStream.toByteArray());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }

        private void addAuthor(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                throws IOException, ClassNotFoundException {
            Author a = (Author)inputStream.readObject();

            boolean result = db.addAuthor(a.getId(), a.getName());

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
        }

        private void deleteAuthor(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                throws IOException {
            long id = inputStream.readLong();
            try{
                boolean result = db.deleteAuthor(id);
                outputStream.writeInt(1);
                outputStream.writeBoolean(result);
            } catch(RuntimeException e){
                outputStream.writeInt(-1);
            }
        }

        private void addBook(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                throws IOException, ClassNotFoundException {
            Book b = (Book)inputStream.readObject();

            boolean result = db.addBook(b.getISBN(), b.getTitle(), b.getYear(), b.getNumberPages(), b.getAuthor().getId());

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
        }

        private void deleteBook(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                throws IOException {
            long isbn = inputStream.readLong();

            boolean result = db.deleteBook(isbn);

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
        }

        private void editBook(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                throws IOException {
            int operationCode = inputStream.readInt();
            long isbn = inputStream.readLong();
            boolean result;

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
                    return;
            }

            outputStream.writeInt(1);
            outputStream.writeBoolean(result);
        }

        private void countBooks(ObjectOutputStream outputStream)
                throws IOException {
            int numberOfBooks = db.getNumberOfBooks();

            outputStream.writeInt(1);
            outputStream.writeInt(numberOfBooks);
        }

        private void getAllBooks(ObjectOutputStream outputStream)
                throws IOException {
            List<Book> books = db.getAllBooks();

            outputStream.writeInt(1);
            outputStream.writeObject(books);
        }

        private void getAllBooksFromAuthor(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                throws IOException {
            long id = inputStream.readLong();

            List<Book> books = db.getAllBooksFromAuthor(id);

            outputStream.writeInt(1);
            outputStream.writeObject(books);
        }

        private void getAllAuthors(ObjectOutputStream outputStream)
                throws IOException {
            List<Author> authors = db.getAllAuthors();

            outputStream.writeInt(1);
            outputStream.writeObject(authors);
        }
    }
}
