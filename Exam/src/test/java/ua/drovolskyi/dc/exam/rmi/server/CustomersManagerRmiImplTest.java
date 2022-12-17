package ua.drovolskyi.dc.exam.rmi.server;

import org.junit.jupiter.api.Test;
import ua.drovolskyi.dc.exam.customer.Customer;
import ua.drovolskyi.dc.exam.customer.CustomersManager;
import ua.drovolskyi.dc.exam.sockets.client.ClientSocketTask2;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.*;

class CustomersManagerRmiImplTest {
    private final int NUMBER_OF_CLIENTS = 10;
    private final int NUMBER_OF_CUSTOMERS = 100;
    private CustomersManagerRmiImpl customersManager;

    public CustomersManagerRmiImplTest() throws RemoteException {
        customersManager = new CustomersManagerRmiImpl(new CustomersManager(NUMBER_OF_CUSTOMERS));
    }

    public boolean areListsEqual(List<Customer> l1, List<Customer>l2){
        l1.sort(new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return Long.compare(o1.getId(), o2. getId());
            }
        });

        l2.sort(new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return Long.compare(o1.getId(), o2. getId());
            }
        });

        return l1.equals(l2);
    }

    @Test
    void testOperations() throws InterruptedException {
        CustomersManager customersManagerLocal = new CustomersManager(NUMBER_OF_CUSTOMERS);
        CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_CLIENTS);

        // create client threads
        Thread[] clientsThreads = new Thread[NUMBER_OF_CLIENTS];
        for(int i = 0; i < NUMBER_OF_CLIENTS; i++){
            final int clientId = i;
            clientsThreads[i] = new Thread(() ->{
                try {
                    // This customer is unique for each client
                    Customer c1 = new Customer(
                            NUMBER_OF_CUSTOMERS + clientId + 1, "name", "surname",
                            "patronymic", "address",
                            234, 5654);

                    // Add non-existent customer
                    assertEquals(customersManagerLocal.addCustomer(c1), customersManager.addCustomer(c1));

                    // Add existent customer
                    assertEquals(customersManagerLocal.addCustomer(c1), customersManager.addCustomer(c1));

                    // wait when all clients write their customers
                    barrier.await();


                    // In alphabet order
                    assertEquals(customersManagerLocal.getCustomersInAlphabetOrder(),
                            customersManager.getCustomersInAlphabetOrder());


                    // Customers with cards in correct non-empty interval
                    assertTrue(areListsEqual(customersManagerLocal.selectInCardInterval(1000, 1200),
                            customersManager.selectInCardInterval(1000, 1200)));

                    // Customers with cards in correct non-empty one-point interval
                    assertTrue(areListsEqual(customersManagerLocal.selectInCardInterval(1200, 1200),
                            customersManager.selectInCardInterval(1200, 1200)));

                    // Customers with cards in correct empty interval
                    assertTrue(areListsEqual(customersManagerLocal.selectInCardInterval(0, 1),
                            customersManager.selectInCardInterval(0, 1)));

                    // Customers with cards in incorrect interval:
                    assertThrows(IllegalArgumentException.class,
                            () ->{
                                customersManager.selectInCardInterval(-100, 100);
                            });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // start client threads
        for(Thread clientThread : clientsThreads){
            clientThread.start();
        }

        // wait for client threads
        for(Thread clientThread : clientsThreads){
            clientThread.join();
        }
    }

}