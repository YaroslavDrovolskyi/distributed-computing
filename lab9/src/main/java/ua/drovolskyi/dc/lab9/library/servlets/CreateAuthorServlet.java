package ua.drovolskyi.dc.lab9.library.servlets;

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

@WebServlet("/library/createAuthor")
public class CreateAuthorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private LibraryDB db;  
    private final Lock writeLock;
    private final Lock readLock;
    
    public CreateAuthorServlet() {
        super();
        
        try {
        	Class.forName("com.mysql.jdbc.Driver");
			db = new LibraryDB();
		} catch (FileNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        ReadWriteLock lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/jsp/createAuthor.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("createAuthorAction");
	    if (action!=null) { // got request with data to create author
	    	long id = Long.valueOf(request.getParameter("authorId")); // must be correct
	    	String name = request.getParameter("authorName");
	    	
	    	writeLock.lock();
	    	boolean result = db.addAuthor(id, name.trim());
	    	writeLock.unlock();
	    	
	    	if(result) { // OK
	    		request.setAttribute("isSuccess", true);	
	    		System.out.println("Created success");
	    	} 
	    	else { // incorrect ID
	    		request.setAttribute("isSuccess", false);
	    		request.setAttribute("errorMessage", "ID must be unique");
	    	}
	    }
	    getServletContext().getRequestDispatcher("/jsp/createAuthor.jsp").forward(request, response);
	}

}
