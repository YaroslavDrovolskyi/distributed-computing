package ua.drovolskyi.dc.exam.customer;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// thread-safe container for customers
public class CustomersManager {
    private List<Customer> customers = new LinkedList<>();
    private Lock writeLock;
    private Lock readLock;

    public CustomersManager(){
        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        writeLock = rwLock.writeLock();
        readLock = rwLock.readLock();
    }
    public CustomersManager(int numberOfCustomers){
        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        writeLock = rwLock.writeLock();
        readLock = rwLock.readLock();

        for(int i = 0; i < numberOfCustomers; i++){
            customers.add(new Customer(
                    i,
                    "name-"+i,
                    "surname-"+i,
                    "patronymic-"+i,
                    "address-"+i,
                    1000 + 50 * i,
                    1000 + 50 * i
                    ));
        }
        Collections.shuffle(customers);
    }

    public boolean addCustomer(Customer c){
        try{
            writeLock.lock();
            for(Customer customer : customers){
                if(customer.getId() == c.getId()){
                    return false;
                }
            }
            return customers.add(c);
        } finally{
            writeLock.unlock();
        }
    }

    public List<Customer> getCustomersInAlphabetOrder(){
        try{
            readLock.lock();
            List<Customer> result = new LinkedList<>();
            result.addAll(customers);

            result.sort(new Comparator<Customer>() {
                @Override
                public int compare(Customer o1, Customer o2) {
                    int namesCompare = o1.getFullName().compareTo(o2.getFullName());
                    if(namesCompare == 0){
                        return Long.compare(o1.getId(), o2.getId());
                    }
                    else{
                        return namesCompare;
                    }
                }
            });
            return result;
        } finally{
            readLock.unlock();
        }
    }

    public List<Customer> selectInCardInterval(long start, long end){
        if(start > end || start < 0){
            throw new IllegalArgumentException("start must be <= end, start >= 0");
        }

        try{
            readLock.lock();
            List<Customer> result = new LinkedList<>();
            for(Customer c : customers){
                if(c.getCreditCardNumber() >= start && c.getCreditCardNumber() <= end){
                    result.add(c);
                }
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }

    public void print(){
        readLock.lock();
        System.out.println("Customers:");
        for(Customer c : customers){
            System.out.println(c);
        }
        readLock.unlock();
    }

    public static void printCustomersList(List<Customer> list){
        for(Customer c : list){
            System.out.println(c);
        }
    }

}
