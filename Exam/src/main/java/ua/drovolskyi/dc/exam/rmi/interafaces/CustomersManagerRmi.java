package ua.drovolskyi.dc.exam.rmi.interafaces;

import ua.drovolskyi.dc.exam.customer.Customer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public interface CustomersManagerRmi extends Remote {
    boolean addCustomer(Customer c) throws RemoteException;
    List<Customer> getCustomersInAlphabetOrder() throws RemoteException;
    List<Customer> selectInCardInterval(long start, long end) throws RemoteException;
}
