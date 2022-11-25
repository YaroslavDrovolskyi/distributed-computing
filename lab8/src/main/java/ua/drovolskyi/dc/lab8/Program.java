package ua.drovolskyi.dc.lab8;

import ua.drovolskyi.dc.lab8.library.Library;
import ua.drovolskyi.dc.lab8.library.LibraryDB;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class Program {
    public static void main(String[] args) {
        LibraryDB db = null;
        try {
            db = new LibraryDB();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println(db.getNumberOfBooks());

        Library lib = db.getLibrary();
        lib.print();
    }
}
