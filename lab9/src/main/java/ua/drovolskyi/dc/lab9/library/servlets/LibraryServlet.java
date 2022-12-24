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
import ua.drovolskyi.dc.lab9.library.SynchronizedLibraryDB;

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
	private SynchronizedLibraryDB db;
	
	public LibraryServlet() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			db = new SynchronizedLibraryDB();
		} catch (FileNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
		// display all authors and books
    	request.setAttribute("authorsList", db.getAllAuthors());
    	request.setAttribute("booksList", db.getAllBooks());
    	forwardToPage("/jsp/library.jsp", request, response);
	}
	
	
	private void forwardToPage(String nextURL, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextURL);
        dispatcher.forward(req, resp);
        System.out.println("Forwarded to: " + nextURL);
	}

	

}