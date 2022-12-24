package ua.drovolskyi.dc.lab9.library.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.drovolskyi.dc.lab9.library.Author;
import ua.drovolskyi.dc.lab9.library.Book;
import ua.drovolskyi.dc.lab9.library.LibraryDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private LibraryDB db;
	private final Lock readLock;
	private final Lock writeLock;
	
	public LibraryServlet() {
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
       
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {
		try {
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String submit = request.getParameter("submitManageItem");
		if(submit != null) {
			if(submit.equals("submitEdit")) {
				forwardToPage("/library/editItem", request, response);
			}
			else if(submit.equals("submitDelete")) {
				forwardToPage("/library/deleteItem", request, response);
			}
			return;
		}
		
		// display all authors and books
    	readLock.lock();
    	request.setAttribute("authorsList", db.getAllAuthors());
    	request.setAttribute("booksList", db.getAllBooks());
    	readLock.unlock();
    	
    	forwardToPage("/jsp/library.jsp", request, response);
		
		
		/*
        if (request.getParameter("manageAuthorWithId") != null){
        	long id = Integer.valueOf(request.getParameter("manageAuthorWithId"));
        	String submit = request.getParameter("submitManageItem");
            switch (submit) {           
            case "submitEdit":
            	System.out.println("Edit author with ID: " + id);
            	//// need to add forwarding to editServlet
                break;           
            case "submitDelete":
            	System.out.println("Delete author with ID: " + id);
          
            	writeLock.lock();
            	boolean result = db.deleteAuthor(id);
            	writeLock.unlock();
            	
            	
                break;
            }
        }
        else if (request.getParameter("manageBookWithISBN") != null){
        	long isbn = Integer.valueOf(request.getParameter("manageBookWithISBN"));
        	String submit = request.getParameter("submitManageItem");
            switch (submit) {           
            case "submitEdit":
            	System.out.println("Edit book with ISBN: " + isbn);
                break;           
            case "submitDelete":
            	System.out.println("Delete book with IDBN: " + isbn);
            	// Need to add forwarding to jsp that will show status of done operation )
            	writeLock.lock();
            	boolean result = db.deleteBook(isbn);
            	writeLock.unlock();
                break;
            }
        }
        */
//        else{
        	
        	
//        }
	}
	
	
	private void forwardToPage(String nextURL, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextURL);
        dispatcher.forward(req, resp);
        System.out.println("Forwarded to: " + nextURL);
	}

	

}

// action in <form> is name of servlet-handler
// we need re-address from servlet to .jsp file
/*
	We can do forward() only one time (it is forward request to some .jsp)
*/