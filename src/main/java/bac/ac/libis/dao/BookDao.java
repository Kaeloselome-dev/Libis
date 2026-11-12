package bac.ac.libis.dao;

import bac.ac.libis.model.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookDao {
    Book findByIsbn(String isbn) throws SQLException;
    boolean create(Book book) throws SQLException;
    boolean update(Book book) throws SQLException;
    boolean delete(int bookId) throws SQLException;
    Book findById(int bookId) throws SQLException;

    List<Book> findAll() throws SQLException;

    List<Book> search(String term, String filterBy)
            throws SQLException;
}
