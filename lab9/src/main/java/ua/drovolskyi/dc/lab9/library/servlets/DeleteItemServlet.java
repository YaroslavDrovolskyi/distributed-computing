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
		if (request.getParameter("requestDeleteItem") != null){
			String action = request.getParameter("requestDeleteItem");
			switch(action) {
			case "deleteAuthor":
				long id = Long.valueOf(request.getParameter("authorId"));
	        	System.out.println("Delete author with ID: " + id);
	      
	        	boolean result = db.deleteAuthor(id);
	        	
	        	request.setAttribute("isSuccess", result);
	        	request.setAttribute("item", "author");
				break;
			case "deleteBook":
				long isbn = Long.valueOf(request.getParameter("bookISBN"));
	        	System.out.println("Delete book with ISBN: " + isbn);
	        			
	        	result = db.deleteBook(isbn);
	        	
	        	request.setAttribute("isSuccess", result);
	        	request.setAttribute("item", "book");
				break;
			}
			getServletContext().getRequestDispatcher("/jsp/deleteItem.jsp").forward(request, response);
        }
        else { // incorrect addressing to this page
        	response.sendRedirect("/library");
        }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
