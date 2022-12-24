package ua.drovolskyi.dc.lab9.library.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.drovolskyi.dc.lab9.library.Author;
import ua.drovolskyi.dc.lab9.library.Book;
import ua.drovolskyi.dc.lab9.library.LibraryDB;
import ua.drovolskyi.dc.lab9.library.SynchronizedLibraryDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebServlet("/library/editItem")
public class EditItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SynchronizedLibraryDB db;
       

    public EditItemServlet() {
        super();
        try {
			Class.forName("com.mysql.jdbc.Driver");
			db = new SynchronizedLibraryDB();
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
		System.out.println("doGet of EditItemServlet");
		if(request.getParameter("requestEditItem")!= null) { // request came from /library page
			System.out.println("Requested for edit item");
			String action = (String) request.getParameter("requestEditItem");
			switch(action) {
			case "editAuthor":
				long id = Long.valueOf(request.getParameter("authorId"));
				Author author = db.getAuthorById(id); 
			
				request.setAttribute("author", author);
	        	getServletContext().getRequestDispatcher("/jsp/editAuthor.jsp").forward(request, response);	
				return;
			case "editBook":
				long isbn = Long.valueOf(request.getParameter("bookISBN"));
				Book book = db.getBookByISBN(isbn);
			
				request.setAttribute("book", book);
	        	getServletContext().getRequestDispatcher("/jsp/editBook.jsp").forward(request, response);	
				return;
			}
		}
		else if(request.getParameter("submitEditItem")!= null) { // request came from edit page
			String action = (String) request.getParameter("submitEditItem");
			switch(action) {
			case "editAuthor":
				long id = Long.valueOf(request.getParameter("authorId"));
		    	String name = request.getParameter("authorName");
		    	
		    	boolean result = db.changeAuthor(id, name);
		    
		    	request.setAttribute("isSuccess", result);
		    	if(result) {
		    		request.setAttribute("author", db.getAuthorById(id));
		    	}
		    	else {
		    		request.setAttribute("errorMessage", "Error: Author with given ID does not exist");
		    	}
		    	getServletContext().getRequestDispatcher("/jsp/editAuthor.jsp").forward(request, response);
				return;
			case "editBook":
				long isbn = Long.valueOf(request.getParameter("bookISBN"));
		    	String title = request.getParameter("bookTitle");
		    	int year = Integer.valueOf(request.getParameter("bookPublishingYear"));
		    	int pages = Integer.valueOf(request.getParameter("bookNumberOfPages"));
		    	long authorId = Long.valueOf(request.getParameter("bookAuthorId"));
		    	
		    	result = db.changeBook(isbn, title, year, pages, authorId);
		    
		    	request.setAttribute("isSuccess", result);
		    	if(result) {
		    		request.setAttribute("book", db.getBookByISBN(isbn));
		    	}
		    	else {
		    		request.setAttribute("errorMessage", "Error: Book does not exist or author with given ID does not exist");
		    	}
		    	getServletContext().getRequestDispatcher("/jsp/editBook.jsp").forward(request, response);
				return;
			}
		}
        else { // incorrect addressing to this page
        	response.sendRedirect("/lab9/library");
        }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
