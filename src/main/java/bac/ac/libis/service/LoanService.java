package bac.ac.libis.service;

import bac.ac.libis.exception.LoanException;

import bac.ac.libis.dao.BookDao;
import bac.ac.libis.dao.BookDaoimpl;
import bac.ac.libis.model.Book;
import bac.ac.libis.dao.LoanDao;
import bac.ac.libis.dao.LoanDaoimpl;
import bac.ac.libis.model.Loan;
import java.sql.SQLException;
import java.util.List;

/** Loan rules live here; controllers only handle HTTP and authenticated identity. */
public class LoanService {
    public static final int MAX_LOANS = 3;
    private final LoanDao loans;
    private final BookDao books;

    public LoanService() {
        this(new LoanDaoimpl(), new BookDaoimpl());
    }

    public LoanService(LoanDao loans, BookDao books) {
        this.loans = loans;
        this.books = books;
    }

    public void borrow(int memberId, int bookId) throws SQLException, LoanException {
        Book book = books.findById(bookId);
        if (book == null) {
            throw new LoanException("This book does not exist.");
        }
        if (loans.hasOpenLoan(bookId)) {
            throw new LoanException("\"" + book.getTitle() + "\" is already on loan.");
        }
        if (loans.countOpenLoans(memberId) >= MAX_LOANS) {
            throw new LoanException("You already hold the maximum of " + MAX_LOANS + " books. Return a book first.");
        }
        loans.createLoan(memberId, bookId);
    }

    public void returnLoan(int memberId, int loanId) throws SQLException, LoanException {
        boolean owned = loans.findOpenLoans(memberId).stream()
                .anyMatch(loan -> loan.getLoanId() == loanId);
        if (!owned) {
            throw new LoanException("You do not have an open loan with that ID.");
        }
        if (!loans.markReturned(memberId, loanId)) {
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

