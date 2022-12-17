package ua.drovolskyi.dc.exam.rmi.server;

import ua.drovolskyi.dc.exam.customer.Customer;
import ua.drovolskyi.dc.exam.customer.CustomersManager;
import ua.drovolskyi.dc.exam.rmi.interafaces.CustomersManagerRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CustomersManagerRmiImpl extends UnicastRemoteObject
        implements CustomersManagerRmi {

    private final CustomersManager customersManager;

    public CustomersManagerRmiImpl(CustomersManager customersManager) throws RemoteException {
        super();

        this.customersManager = customersManager;
    }

    @Override
    public boolean addCustomer(Customer c) throws RemoteException {
        return customersManager.addCustomer(c);
    }

    @Override
    public List<Customer> getCustomersInAlphabetOrder() throws RemoteException {
        return customersManager.getCustomersInAlphabetOrder();
    }

    @Override
    public List<Customer> selectInCardInterval(long start, long end) throws RemoteException {
        return customersManager.selectInCardInterval(start, end);
    }
}
