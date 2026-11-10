
package bac.ac.libis.dao;

import bac.ac.libis.config.DatabaseConfig;
import bac.ac.libis.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDao implements BookDaoimpl {

    private static final String FIND_ALL_SQL = """
            SELECT b.*,
                NOT EXISTS (
                    SELECT 1 FROM loans l
                    WHERE l.book_id = b.book_id
                    AND l.returned_on IS NULL
                ) AS available
            FROM books b
            """;

    private static final String SEARCH_TITLE_SQL = """
            SELECT b.*,
                NOT EXISTS (
                    SELECT 1 FROM loans l
                    WHERE l.book_id = b.book_id
                    AND l.returned_on IS NULL
                ) AS available
            FROM books b
            WHERE b.title LIKE ?
            """;

    private static final String SEARCH_AUTHOR_SQL = """
            SELECT b.*,
                NOT EXISTS (
                    SELECT 1 FROM loans l
                    WHERE l.book_id = b.book_id
                    AND l.returned_on IS NULL
                ) AS available
            FROM books b
            WHERE b.author LIKE ?
            """;

    private static final String SEARCH_ALL_SQL = """
            SELECT b.*,
                NOT EXISTS (
                    SELECT 1 FROM loans l
                    WHERE l.book_id = b.book_id
                    AND l.returned_on IS NULL
                ) AS available
            FROM books b
            WHERE b.title LIKE ?
               OR b.author LIKE ?
            """;

    @Override
    public List<Book> findAll() throws SQLException {

        List<Book> books = new ArrayList<>();

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pst =
                        conn.prepareStatement(FIND_ALL_SQL);
                ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                books.add(mapBook(rs));
            }
        }

        return books;
    }

    @Override
    public List<Book> search(String term, String filterBy)
            throws SQLException {

        if (term == null || term.trim().isEmpty()) {
            return findAll();
        }

        List<Book> books = new ArrayList<>();
        String pattern = "%" + term.trim() + "%";

        String sql;
        boolean searchBoth = false;

        if ("title".equals(filterBy)) {
            sql = SEARCH_TITLE_SQL;
        } else if ("author".equals(filterBy)) {
            sql = SEARCH_AUTHOR_SQL;
        } else {
            sql = SEARCH_ALL_SQL;
            searchBoth = true;
        }

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, pattern);

            if (searchBoth) {
                pst.setString(2, pattern);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }

        return books;
    }

    private Book mapBook(ResultSet rs) throws SQLException {

        Book book = new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getString("synopsis"),
                rs.getBoolean("available")
        );

        book.setIsbn(rs.getString("isbn"));

        return book;
    }
}
