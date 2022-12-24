package ua.drovolskyi.dc.lab9.library;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedLibraryDB extends LibraryDB{
	private Lock writeLock;
	private Lock readLock;

	public SynchronizedLibraryDB() throws FileNotFoundException, SQLException {
		super();
		ReadWriteLock rwLock = new ReentrantReadWriteLock();
		writeLock = rwLock.writeLock();
		readLock = rwLock.readLock();
	}

	@Override
	public boolean addAuthor(long id, String name) {
		writeLock.lock();
		try {
			return super.addAuthor(id, name);
		} finally {
			writeLock.unlock();
		}
	}
	
	public boolean changeAuthor(long id, String name) {
		writeLock.lock();
		try {
			return super.changeAuthorName(id, name);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean addBook(long isbn, String title, int year, int pages, long authorId) {
		writeLock.lock();
		try {
			return super.addBook(isbn, title, year, pages, authorId);
		} finally {
			writeLock.unlock();
		}
	}
	
	public boolean changeBook(long isbn, String title, int year, int pages, long authorId) {
		writeLock.lock();
		try {
			super.changeBookTitle(isbn, title);
			super.changeBookYear(isbn, year);
			super.changeBookNumberOfPages(isbn, pages);
			return super.changeBookAuthor(isbn, authorId);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean deleteAllAuthors() {
		writeLock.lock();
		try {
			return super.deleteAllAuthors();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean deleteAllBooks() {
		writeLock.lock();
		try {
			return super.deleteAllBooks();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean deleteAuthor(long id) {
		writeLock.lock();
		try {
			return super.deleteAuthor(id);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean deleteBook(long isbn) {
		writeLock.lock();
		try {
			return super.deleteBook(isbn);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Author getAuthorById(long id) {
		readLock.lock();
		try {
			return super.getAuthorById(id);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Book getBookByISBN(long isbn) {
		readLock.lock();
		try {
			return super.getBookByISBN(isbn);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean changeAuthorName(long id, String newName) {
		writeLock.lock();
		try {
			return super.changeAuthorName(id, newName);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean changeBookTitle(long isbn, String newTitle) {
		writeLock.lock();
		try {
			return super.changeBookTitle(isbn, newTitle);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean changeBookYear(long isbn, int year) {
		writeLock.lock();
		try {
			return super.changeBookYear(isbn, year);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean changeBookNumberOfPages(long isbn, int pages) {
		writeLock.lock();
		try {
			return super.changeBookNumberOfPages(isbn, pages);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean changeBookAuthor(long isbn, long authorId) {
		writeLock.lock();
		try {
			return super.changeBookAuthor(isbn, authorId);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public List<Author> getAllAuthors() {
		readLock.lock();
		try {
			return super.getAllAuthors();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<Book> getAllBooks() {
		readLock.lock();
		try {
			return super.getAllBooks();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public List<Book> getAllBooksFromAuthor(long authorId) {
		readLock.lock();
		try {
			return super.getAllBooksFromAuthor(authorId);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Library getLibrary() {
		readLock.lock();
		try {
			return super.getLibrary();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public int getNumberOfBooks() {
		readLock.lock();
		try {
			return super.getNumberOfBooks();
		} finally {
			readLock.unlock();
		}
	}
}
