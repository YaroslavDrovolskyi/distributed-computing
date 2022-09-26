package ua.edu.yarik.task_a;

public class PhoneNumber {
    private String number;

    public PhoneNumber(String number){
        this.number = number;
    }

    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o == null || this.getClass() != o.getClass()){
            return false;
        }
        
        if (this.number == ((PhoneNumber) o).number){
            return true;
        }
        return false;
    }
}
