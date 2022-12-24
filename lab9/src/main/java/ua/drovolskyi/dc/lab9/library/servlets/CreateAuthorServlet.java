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

@WebServlet("/library/createAuthor")
public class CreateAuthorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private SynchronizedLibraryDB db;
    
    public CreateAuthorServlet() {
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
		getServletContext().getRequestDispatcher("/jsp/createAuthor.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("createAuthorAction");
	    if (action!=null) { // got request with data to create author
	    	long id = Long.valueOf(request.getParameter("authorId")); // must be correct
	    	String name = request.getParameter("authorName");
	    	
	    	boolean result = db.addAuthor(id, name.trim());
	    	
	    	if(result) { // OK
	    		request.setAttribute("isSuccess", true);	
	    		System.out.println("Created author successfully");
	    	} 
	    	else { // incorrect ID
	    		request.setAttribute("isSuccess", false);
	    		request.setAttribute("errorMessage", "ID must be unique");
	    	}
	    }
	    getServletContext().getRequestDispatcher("/jsp/createAuthor.jsp").forward(request, response);
	}

}
