package ua.drovolskyi.dc.lab9.library.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.drovolskyi.dc.lab9.library.LibraryDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebServlet("/library/deleteItem")
public class DeleteItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private LibraryDB db;
	private final Lock readLock;
	private final Lock writeLock;
	
    public DeleteItemServlet() {
    	try {
			Class.forName("com.mysql.jdbc.Driver");
			db = new LibraryDB();
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ReadWriteLock lock = new ReentrantReadWriteLock();
		writeLock = lock.writeLock();
		readLock = lock.readLock();	
	}
       
	public void destroy() {
		try {
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("manageAuthorWithId") != null){
        	long id = Long.valueOf(request.getParameter("manageAuthorWithId"));
        	System.out.println("Delete author with ID: " + id);
      
        	writeLock.lock();
        	boolean result = db.deleteAuthor(id);
        	writeLock.unlock();
        	
        	request.setAttribute("isSuccess", result);
        	request.setAttribute("item", "author");
        	getServletContext().getRequestDispatcher("/jsp/deleteItem.jsp").forward(request, response);
        }
        else if (request.getParameter("manageBookWithISBN") != null){
        	long isbn = Long.valueOf(request.getParameter("manageBookWithISBN"));
        	System.out.println("Delete book with IDBN: " + isbn);
        			
        	writeLock.lock();
        	boolean result = db.deleteBook(isbn);
        	writeLock.unlock();
        	
        	request.setAttribute("isSuccess", result);
        	request.setAttribute("item", "book");
        	getServletContext().getRequestDispatcher("/jsp/deleteItem.jsp").forward(request, response);
        }
        else {
        	// incorrect addressing to this page
        	getServletContext().getRequestDispatcher("/library").forward(request, response);
        }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
