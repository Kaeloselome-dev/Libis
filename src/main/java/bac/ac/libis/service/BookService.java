package bac.ac.libis.service;

import bac.ac.libis.dao.BookDao;
import bac.ac.libis.dao.BookDaoimpl;
import bac.ac.libis.exception.ValidationException;
import bac.ac.libis.model.Book;
import java.sql.SQLException;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private final BookDao books;
    public BookService() { this(new BookDaoimpl()); }
    public BookService(BookDao books) { this.books = books; }
    public List<Book> search(String term, String filter) throws SQLException { return books.search(term, filter); }
    public Book findById(int id) throws SQLException { return books.findById(id); }

    public void save(Book book, String year) throws SQLException, ValidationException {
        Map<String, String> errors = new LinkedHashMap<>();
        required(errors, "isbn", book.getIsbn(), 20);
        required(errors, "title", book.getTitle(), 150);
        required(errors, "author", book.getAuthor(), 100);
        if (book.getGenre() == null) book.setGenre("");
        if (book.getGenre().length() > 50) errors.put("genre", "Use at most 50 characters.");
        book.setPublicationYear(null);
        if (year != null && !year.isBlank()) {
            try {
                if (!year.matches("[0-9]+")) throw new NumberFormatException();
                int value = Integer.parseInt(year);
                if (value < 1 || value > Year.now().getValue()) throw new NumberFormatException();
                book.setPublicationYear(value);
            } catch (NumberFormatException e) {
                errors.put("publicationYear", "Enter a whole-number year between 1 and " + Year.now().getValue() + ".");
            }
        }
        Book duplicate = books.findByIsbn(book.getIsbn());
        if (duplicate != null && duplicate.getBookId() != book.getBookId()) errors.put("isbn", "This ISBN is already in use.");
        if (!errors.isEmpty()) throw new ValidationException(errors);
        if (book.getBookId() != 0 && books.findById(book.getBookId()) == null)
            throw new ValidationException(Map.of("form", "This book no longer exists."));
        try {
            boolean saved = book.getBookId() == 0 ? books.create(book) : books.update(book);
            if (!saved) throw new ValidationException(Map.of("form", "This book could not be saved. Reload and try again."));
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new ValidationException(Map.of("isbn", "This ISBN is already in use."));
        }
    }

    private void required(Map<String, String> errors, String key, String value, int max) {
        if (value == null || value.isBlank()) errors.put(key, "This field is required.");
        else if (value.length() > max) errors.put(key, "Use at most " + max + " characters.");
    }

    public String delete(int id) throws SQLException, ValidationException {
        Book book = books.findById(id);
        if (book == null) throw new ValidationException(Map.of("form", "This book no longer exists."));
        if (!book.isAvailable()) throw new ValidationException(Map.of("form", "This book is currently on loan and cannot be deleted."));
        if (!books.delete(id)) throw new ValidationException(Map.of("form", "Book not deleted. It may have been borrowed or removed. Reload and try again."));
        return "Deleted book " + book.getTitle() + " (" + book.getIsbn() + ").";
    }
}
