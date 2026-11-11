package bac.ac.libis.service;

import bac.ac.libis.dao.LoanDao;
import bac.ac.libis.dao.LoanDaoimpl;
import bac.ac.libis.model.Loan;
import java.sql.SQLException;
import java.util.List;

/** Loan rules live here; controllers only handle HTTP and authenticated identity. */
public class LoanService {
    public static final int MAX_LOANS = 3;
    private final LoanDao loans;

    public LoanService() {
        this(new LoanDaoimpl());
    }

    public LoanService(LoanDao loans) {
        this.loans = loans;
    }

    public void borrow(int memberId, int bookId) throws SQLException, LoanException {
        if (!loans.bookExists(bookId)) {
            throw new LoanException("This book does not exist.");
        }
        if (loans.hasOpenLoan(bookId)) {
            throw new LoanException("This book is already on loan.");
        }
        if (loans.countOpenLoans(memberId) >= MAX_LOANS) {
            throw new LoanException("You may have at most " + MAX_LOANS + " open loans.");
        }
        loans.createLoan(memberId, bookId);
    }

    public void returnLoan(int memberId, int loanId) throws SQLException, LoanException {
        boolean owned = loans.findOpenLoans(memberId).stream()
                .anyMatch(loan -> loan.getLoanId() == loanId);
        if (!owned) {
            throw new LoanException("You do not have an open loan with that ID.");
        }
        if (!loans.markReturned(loanId)) {
            throw new LoanException("This loan has already been returned.");
        }
    }

    public List<Loan> findOpenLoans(int memberId) throws SQLException {
        return loans.findOpenLoans(memberId);
    }

    public List<Loan> findHistory(int memberId) throws SQLException {
        return loans.findHistory(memberId);
    }
}

