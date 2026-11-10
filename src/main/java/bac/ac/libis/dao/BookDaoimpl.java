package bac.ac.libis.dao;

import bac.ac.libis.model.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookDaoimpl {
    List<Book> findAll() throws SQLException;

    List<Book> search(String term, String filterBy)
            throws SQLException;
}
