package ua.drovolskyi.dc.lab9.library.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.drovolskyi.dc.lab9.library.LibraryDB;
import ua.drovolskyi.dc.lab9.library.SynchronizedLibraryDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebServlet("/library/createBook")
public class CreateBookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private SynchronizedLibraryDB db;
    
    public CreateBookServlet() {
        super();
        
        try {
        	Class.forName("com.mysql.jdbc.Driver");
			db = new SynchronizedLibraryDB();
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public void destroy() {
		try {
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/jsp/createBook.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("createBookAction");
	    if (action!=null) { // got request with data to create author
	    	long isbn = Long.valueOf(request.getParameter("bookISBN"));
	    	String title = request.getParameter("bookTitle");
	    	int year = Integer.valueOf(request.getParameter("bookPublishingYear"));
	    	int pages = Integer.valueOf(request.getParameter("bookNumberOfPages"));
	    	long authorId = Long.valueOf(request.getParameter("bookAuthorId"));
	    	
	    	boolean result = db.addBook(isbn, title, year, pages, authorId);
	    	
	    	if(result) { // OK
	    		request.setAttribute("isSuccess", true);	
	    		System.out.println("Created book successfully");
	    	} 
	    	else { // incorrect
	    		request.setAttribute("isSuccess", false);
	    		request.setAttribute("errorMessage", "ISBN must be unique, and author with following ID must exist");
	    	}
	    }
	    getServletContext().getRequestDispatcher("/jsp/createBook.jsp").forward(request, response);
	}

}
