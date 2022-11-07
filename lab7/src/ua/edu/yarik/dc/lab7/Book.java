package ua.edu.yarik.dc.lab7;

public class Book {
    private long isbn;
    private String title;
    private int year;
    private int numberOfPages;
    private Author author;

    public Book(long isbn, String title, int year, int pages, Author author){
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.numberOfPages = pages;
        this.author = author;
    }

    public long getISBN(){
        return isbn;
    }

    public Author getAuthor(){
        return author;
    }

    public String getTitle(){
        return title;
    }

    public int getYear(){
        return year;
    }

    public int getNumberPages(){
        return numberOfPages;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{Book ");
        sb.append("ISBN: " + isbn + ", ");
        sb.append("'" + title + "', ");
        sb.append(author.toString() + ", ");
        sb.append(year + " year, ");
        sb.append(numberOfPages + " pages");
        sb.append("}");
        return sb.toString();
    }
}
