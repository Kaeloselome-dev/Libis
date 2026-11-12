package bac.ac.libis.dao;

import bac.ac.libis.model.Loan;
import java.sql.SQLException;
import java.util.List;

public interface LoanDao {
    /** Creates an open loan dated today by the database; returns its generated ID. */
    int createLoan(int memberId, int bookId) throws SQLException;

    /** Returns false when the loan does not exist or has already been returned. */
    boolean markReturned(int memberId, int loanId) throws SQLException;

    int countOpenLoans(int memberId) throws SQLException;

    List<Loan> findOpenLoans(int memberId) throws SQLException;

    /** Returned loans only, including both borrowing and return dates. */
    List<Loan> findHistory(int memberId) throws SQLException;

    boolean hasOpenLoan(int bookId) throws SQLException;
}
