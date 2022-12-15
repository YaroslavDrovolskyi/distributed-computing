package ua.drovolskyi.dc.lab8.rmi.server;

import ua.drovolskyi.dc.lab8.rmi.interfaces.LibraryRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            // create local registry service
            Registry registry = LocateRegistry.createRegistry(1099);

            // create remote object
            LibraryRMI server = new LibraryRMIImpl();

            // register remote object (server) in the registry
            registry.rebind("Library", server);

            System.out.println("Server started");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
