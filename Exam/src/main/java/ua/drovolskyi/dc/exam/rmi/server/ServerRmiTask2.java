package ua.drovolskyi.dc.exam.rmi.server;

import ua.drovolskyi.dc.exam.customer.CustomersManager;
import ua.drovolskyi.dc.exam.rmi.interafaces.CustomersManagerRmi;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRmiTask2 {
    public static void main(String[] args) {
        try {
            // create local registry service
            Registry registry = LocateRegistry.createRegistry(1099);

            // create remote object
            CustomersManager customersManager = new CustomersManager(10);
            CustomersManagerRmi server = new CustomersManagerRmiImpl(customersManager);

            // register remote object (server) in the registry
            registry.rebind("CustomersManager", server);

            System.out.println("Server started");
        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
