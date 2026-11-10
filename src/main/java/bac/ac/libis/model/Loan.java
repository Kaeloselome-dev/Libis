package bac.ac.libis.model;

import java.time.LocalDate;

public class Loan {

    private int loanId;
    private int memberId;
    private int bookId;
    private Book book;
    private LocalDate borrowedOn;
    private LocalDate returnedOn;

    public Loan() {
    }

    public Loan(int loanId, int memberId, int bookId,
                Book book, LocalDate borrowedOn,
                LocalDate returnedOn) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.book = book;
        this.borrowedOn = borrowedOn;
        this.returnedOn = returnedOn;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public void setBorrowedOn(LocalDate borrowedOn) {
        this.borrowedOn = borrowedOn;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public void setReturnedOn(LocalDate returnedOn) {
        this.returnedOn = returnedOn;
    }

    public boolean isReturned() {
        return returnedOn != null;
    }
}
