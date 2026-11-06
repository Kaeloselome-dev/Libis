package bac.ac.libis.model;

import java.time.LocalDate;

public class Loan {
   private int Loanid;
    private  int Memberid;
    private int Bookid;
    private Book book;
    private LocalDate borrowedOn;
    private  LocalDate returnedOn;
    private String Description;


    public Loan(int loanid, int memberid, int bookid, Book book, LocalDate borrowedOn, LocalDate returnedOn, String description, boolean isReturned) {
        Loanid = loanid;
        Memberid = memberid;
        Bookid = bookid;
        this.book = book;
        borrowedOn = borrowedOn;
        returnedOn = returnedOn;
        Description = description;

    }

    public Loan() {
    }

    public int getLoanid() {
        return Loanid;
    }

    public void setLoanid(int loanid) {
        Loanid = loanid;
    }

    public int getMemberid() {
        return Memberid;
    }

    public void setMemberid(int memberid) {
        Memberid = memberid;
    }

    public int getBookid() {
        return Bookid;
    }

    public void setBookid(int bookid) {
        Bookid = bookid;
    }



    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isReturned() {
        return returnedOn != null;
    }
}
