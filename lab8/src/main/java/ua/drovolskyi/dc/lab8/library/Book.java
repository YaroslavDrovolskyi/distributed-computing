package ua.drovolskyi.dc.lab8.library;

import java.io.Serializable;

public class Book implements Serializable {
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

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(!(o instanceof Book)){
            return false;
        }

        Book other = (Book) o;

        return isbn == other.isbn && title.equals(other.title) &&
                year == other.year && numberOfPages == other.numberOfPages &&
                author.getId() == other.author.getId();
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setNumberOfPages(int pages){
        this.numberOfPages = pages;
    }

    public void setAuthor(Author author){
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
