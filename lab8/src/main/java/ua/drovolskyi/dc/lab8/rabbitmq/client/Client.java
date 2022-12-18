package ua.drovolskyi.dc.lab8.rabbitmq.client;

import com.rabbitmq.client.*;

import java.io.IOException;
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

    @Override
    public void close() throws IOException {
        channel.queueDelete(responseQueueName); // delete queue for responses
        connection.close();
    }
}
