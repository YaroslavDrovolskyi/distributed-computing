package ua.drovolskyi.dc.exam;


import ua.drovolskyi.dc.exam.customer.Customer;
import ua.drovolskyi.dc.exam.customer.CustomersManager;

// Program to demonstrate work of CustomersManager
public class Program {
    public static void main(String[] args){
        CustomersManager manager = new CustomersManager(10);
        manager.print();

        System.out.print("\nAdd non-existent customer: ");
        System.out.println(manager.addCustomer(new Customer(
                100, "rfgdf", "efrgr", "efrgth", "some address",
                234, 5654)));

        System.out.print("Add existent customer: ");
        System.out.println(manager.addCustomer(new Customer(
                0,"rfgdf", "efrgr", "efrgth", "some address",
                234, 5654)));

        System.out.println("\nIn alphabet order:");
        CustomersManager.printCustomersList(manager.getCustomersInAlphabetOrder());

        System.out.println("\nCustomers with cards in correct non-empty interval:");
        CustomersManager.printCustomersList(manager.selectInCardInterval(1000, 1200));

        System.out.println("\nCustomers with cards in correct non-empty one-point interval:");
        CustomersManager.printCustomersList(manager.selectInCardInterval(1200, 1200));

        System.out.println("\nCustomers with cards in correct empty interval:");
        CustomersManager.printCustomersList(manager.selectInCardInterval(5000, 10000));

        System.out.println("\nCustomers with cards in incorrect interval:");
        CustomersManager.printCustomersList(manager.selectInCardInterval(-100, 100));
    }
}
