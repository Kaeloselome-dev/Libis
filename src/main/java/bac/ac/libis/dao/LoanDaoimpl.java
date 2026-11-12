package bac.ac.libis.dao;

import bac.ac.libis.config.DatabaseConfig;
import bac.ac.libis.model.Loan;
import bac.ac.libis.model.Book;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoanDaoimpl implements LoanDao {
    private static final String SELECT_LOANS = """
            SELECT l.loan_id, l.member_id, l.book_id, l.borrowed_on, l.returned_on,
                   b.title, b.author
            FROM loans l JOIN books b ON b.book_id = l.book_id
            """;

    @Override
    public int createLoan(int memberId, int bookId) throws SQLException {
        String sql = """
                INSERT INTO loans (member_id, book_id, borrowed_on, returned_on)
                VALUES (?, ?, CURRENT_DATE, NULL)
                """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, memberId);
            pst.setInt(2, bookId);
            if (pst.executeUpdate() != 1) {
                throw new SQLException("Unable to create loan.");
            }
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No generated loan ID was returned.");
                }
                return keys.getInt(1);
            }
        }
    }

    @Override
    public boolean markReturned(int memberId, int loanId) throws SQLException {
        String sql = """
                UPDATE loans SET returned_on = CURRENT_DATE
                WHERE loan_id = ? AND member_id = ? AND returned_on IS NULL
                """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, loanId);
            pst.setInt(2, memberId);
            return pst.executeUpdate() == 1;
        }
    }

    @Override
    public int countOpenLoans(int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans l WHERE l.member_id = ? AND l.returned_on IS NULL";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, memberId);
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public List<Loan> findOpenLoans(int memberId) throws SQLException {
        return findLoans(SELECT_LOANS + """
                 WHERE l.member_id = ? AND l.returned_on IS NULL
                 ORDER BY borrowed_on DESC, loan_id DESC
                """, memberId);
    }

    @Override
    public List<Loan> findHistory(int memberId) throws SQLException {
        return findLoans(SELECT_LOANS + """
                 WHERE l.member_id = ? AND l.returned_on IS NOT NULL
                 ORDER BY returned_on DESC, loan_id DESC
                """, memberId);
    }

    @Override
    public boolean hasOpenLoan(int bookId) throws SQLException {
        String sql = "SELECT 1 FROM loans WHERE book_id = ? AND returned_on IS NULL LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bookId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<Loan> findLoans(String sql, int memberId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, memberId);
            try (ResultSet rs = pst.executeQuery()) {
                List<Loan> loans = new ArrayList<>();
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    Date returnedOn = rs.getDate("returned_on");
                    loans.add(new Loan(
                            rs.getInt("loan_id"),
                            rs.getInt("member_id"),
                            rs.getInt("book_id"),
                            book,
                            rs.getDate("borrowed_on").toLocalDate(),
                            returnedOn == null ? null : returnedOn.toLocalDate()));
                }
                return loans;
            }
        }
    }
}
