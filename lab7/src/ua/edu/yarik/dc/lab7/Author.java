package ua.edu.yarik.dc.lab7;

public class Author {
    private long id;
    private String name;

    public Author(long id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(!(o instanceof Author)){
            return false;
        }

        Author other = (Author) o;

        return id == other.id && name.equals(other.name);
    }

    public void setName(String name){
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
