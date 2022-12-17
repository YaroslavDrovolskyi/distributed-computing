package ua.drovolskyi.dc.exam.rmi.client;

import ua.drovolskyi.dc.exam.customer.Customer;
import ua.drovolskyi.dc.exam.customer.CustomersManager;
import ua.drovolskyi.dc.exam.rmi.interafaces.CustomersManagerRmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientRmiTask2 {
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        CustomersManagerRmi customerManager = (CustomersManagerRmi) Naming.lookup("//localhost:1099/CustomersManager");
        System.out.println("RMI object found");



        System.out.print("\nAdd non-existent customer: ");
        System.out.println(customerManager.addCustomer(new Customer(
                100, "rfgdf", "efrgr", "efrgth", "some address",
                234, 5654)));

        System.out.print("Add existent customer: ");
        System.out.println(customerManager.addCustomer(new Customer(
                0,"rfgdf", "efrgr", "efrgth", "some address",
                234, 5654)));

        System.out.println("\nIn alphabet order:");
        CustomersManager.printCustomersList(customerManager.getCustomersInAlphabetOrder());

        System.out.println("\nCustomers with cards in correct non-empty interval:");
        CustomersManager.printCustomersList(customerManager.selectInCardInterval(1000, 1200));

        System.out.println("\nCustomers with cards in correct non-empty one-point interval:");
        CustomersManager.printCustomersList(customerManager.selectInCardInterval(1200, 1200));

        System.out.println("\nCustomers with cards in correct empty interval:");
        CustomersManager.printCustomersList(customerManager.selectInCardInterval(5000, 10000));

        System.out.println("\nCustomers with cards in incorrect interval:");
        CustomersManager.printCustomersList(customerManager.selectInCardInterval(-100, 100));
    }
}
