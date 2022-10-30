package ua.edu.yarik;

public class Author {
    private long id;
    private String name;

    public Author(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{Author ");
        sb.append("ID: " + id + ", ");
        sb.append(name + "}");
        return sb.toString();
    }
}
