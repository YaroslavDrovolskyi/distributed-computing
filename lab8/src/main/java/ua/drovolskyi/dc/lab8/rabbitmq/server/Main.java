package ua.drovolskyi.dc.lab8.rabbitmq.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException, TimeoutException {
        Server server = new Server();
    }
}
