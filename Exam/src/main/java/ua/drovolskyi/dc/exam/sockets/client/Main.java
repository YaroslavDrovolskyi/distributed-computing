package ua.drovolskyi.dc.exam.sockets.client;

import ua.drovolskyi.dc.exam.customer.Customer;
import ua.drovolskyi.dc.exam.customer.CustomersManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientSocketTask2 client = new ClientSocketTask2("localhost", 50000);

        System.out.print("\nAdd non-existent customer: ");
        System.out.println(client.addCustomer(new Customer(
                100, "rfgdf", "efrgr", "efrgth", "some address",
                234, 5654)));

        System.out.print("Add existent customer: ");
        System.out.println(client.addCustomer(new Customer(
                0,"rfgdf", "efrgr", "efrgth", "some address",
                234, 5654)));

        System.out.println("\nIn alphabet order:");
        CustomersManager.printCustomersList(client.getCustomersInAlphabetOrder());

        System.out.println("\nCustomers with cards in correct non-empty interval:");
        CustomersManager.printCustomersList(client.selectInCardInterval(1000, 1200));

        System.out.println("\nCustomers with cards in correct non-empty one-point interval:");
        CustomersManager.printCustomersList(client.selectInCardInterval(1200, 1200));

        System.out.println("\nCustomers with cards in correct empty interval:");
        CustomersManager.printCustomersList(client.selectInCardInterval(5000, 10000));

        System.out.println("\nCustomers with cards in incorrect interval:");
        CustomersManager.printCustomersList(client.selectInCardInterval(-100, 100));
    }
}
