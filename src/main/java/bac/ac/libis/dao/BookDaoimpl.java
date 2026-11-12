
package bac.ac.libis.dao;

import bac.ac.libis.config.DatabaseConfig;
import bac.ac.libis.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDaoimpl implements BookDao {

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
    public Book findById(int bookId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(FIND_ALL_SQL + " WHERE b.book_id = ?")) {
            pst.setInt(1, bookId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapBook(rs) : null;
            }
        }
    }

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

    @Override
    public Book findByIsbn(String isbn) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(FIND_ALL_SQL + " WHERE b.isbn=?")) {
            pst.setString(1, isbn);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapBook(rs) : null;
            }
        }
    }

    @Override
    public boolean create(Book book) throws SQLException {
        return save(book, false);
    }

    @Override
    public boolean update(Book book) throws SQLException {
        return save(book, true);
    }

    private boolean save(Book book, boolean update) throws SQLException {
        String sql = update
                ? "UPDATE books SET isbn=?, title=?, author=?, genre=?, synopsis=?, publication_year=? WHERE book_id=?"
                : "INSERT INTO books (isbn,title,author,genre,synopsis,publication_year) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, book.getIsbn());
            pst.setString(2, book.getTitle());
            pst.setString(3, book.getAuthor());
            pst.setString(4, book.getGenre());
            pst.setString(5, book.getSynopsis());
            pst.setObject(6, book.getPublicationYear(), java.sql.Types.INTEGER);
            if (update) pst.setInt(7, book.getBookId());
            return pst.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int bookId) throws SQLException {
        // Keep the open-loan condition in the DELETE too; historical rows cascade in MySQL.
        String sql = "DELETE FROM books WHERE book_id=? AND NOT EXISTS "
                + "(SELECT 1 FROM loans WHERE loans.book_id=books.book_id AND returned_on IS NULL)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            return pst.executeUpdate() == 1;
        }
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
        // Older databases may not have the optional Part C column yet.
        // Keep catalogue/search/borrowing reads working before the migration.
        java.sql.ResultSetMetaData columns = rs.getMetaData();
        for (int i = 1; i <= columns.getColumnCount(); i++) {
            if ("publication_year".equalsIgnoreCase(columns.getColumnLabel(i))) {
                Number year = (Number) rs.getObject(i);
                book.setPublicationYear(year == null ? null : year.intValue());
                break;
            }
        }

        return book;
    }
}
