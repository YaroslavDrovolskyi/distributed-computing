package ua.drovolskyi.dc.exam.customer;

import java.io.Serializable;

public class Customer implements Serializable {
    private final long id;
    private String name;
    private String surname;
    private String patronymic;
    private String address;
    private long creditCardNumber;
    private long bankAccountNumber;

    public Customer(long id, String name, String surname, String patronymic, String address,
                    long creditCardNumber, long bankAccountNumber){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.address = address;
        this.bankAccountNumber = bankAccountNumber;
        this.creditCardNumber = creditCardNumber;
    }

    // set patronymic="", and number of credit card = number of bank account
    public Customer(long id, String name, String surname, String address, long bankAccountNumber){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = "";
        this.address = address;
        this.bankAccountNumber = bankAccountNumber;
        this.creditCardNumber = bankAccountNumber;
    }

    @Override
    public String toString(){
        return "Customer{" +
                "id: " + id + ", " +
                getFullName() + ", " +
                "address: " + address + ", " +
                "account: " + bankAccountNumber + ", " +
                "card: " + creditCardNumber + "}";
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(!(o instanceof Customer)){
            return false;
        }

        Customer other = (Customer) o;

        return getId() == other.getId() &&
                getFullName().equals(other.getFullName()) &&
                getAddress().equals(other.getAddress()) &&
                getBankAccountNumber() == other.getBankAccountNumber() &&
                getCreditCardNumber() == other.getCreditCardNumber();
    }

    public long getId(){
        return this.id;
    }
    public String getFullName(){
        return surname + " " + name + " " + patronymic;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(long creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public long getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(long bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
}
