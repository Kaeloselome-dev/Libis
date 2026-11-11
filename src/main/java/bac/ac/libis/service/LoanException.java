package bac.ac.libis.service;

/** A loan rule was broken; the message explains the reason to the caller. */
public class LoanException extends Exception {
    public LoanException(String message) {
        super(message);
    }
}
