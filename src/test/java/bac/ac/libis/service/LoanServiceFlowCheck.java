package bac.ac.libis.service;

import bac.ac.libis.dao.LoanDao;
import bac.ac.libis.model.Loan;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Standalone service rule checks with an in-memory DAO stub. */
public class LoanServiceFlowCheck {
    public static void main(String[] args) throws Exception {
        State s = new State(); s.exists = false; rejected(s, "does not exist");
        s = new State(); s.busy = true; rejected(s, "already on loan");
        for (int count : new int[]{3, 4}) {
            s = new State(); s.count = count; rejected(s, "at most 3");
        }
        s = new State(); s.count = 2; s.service.borrow(7, 12);
        check(s.created == 1, "third loan allowed");
        s = new State();
        try { s.service.returnLoan(7, 99); throw new AssertionError("foreign loan accepted"); }
        catch (LoanException expected) { check(s.updated == 0, "foreign loan never updated"); }
        s = new State(); s.service.returnLoan(7, 15);
        check(s.updated == 1, "owned open loan returned");
        s = new State(); s.updateResult = false;
        try { s.service.returnLoan(7, 15); throw new AssertionError("repeat return accepted"); }
        catch (LoanException expected) { check(expected.getMessage().contains("already been returned"), "repeat reason"); }
        s = new State(); s.fail = true;
        try { s.service.borrow(7, 12); throw new AssertionError("SQL failure hidden"); }
        catch (SQLException expected) { check(s.created == 0, "SQL failure prevents write"); }
        s = new State();
        check(s.service.findOpenLoans(7).size() == 1 && s.service.findHistory(7).isEmpty(), "list delegation");
        System.out.println("Passed 10 LoanService checks.");
    }
    static void rejected(State s, String reason) throws Exception {
        try { s.service.borrow(7, 12); throw new AssertionError("borrow accepted"); }
        catch (LoanException expected) {
            check(expected.getMessage().contains(reason), "specific rejection reason");
            check(s.created == 0, "rejected borrow never written");
        }
    }
    static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
    static class State {
        boolean exists = true, busy, updateResult = true, fail;
        int count, created, updated;
        LoanService service = new LoanService((LoanDao) Proxy.newProxyInstance(
                LoanDao.class.getClassLoader(), new Class<?>[]{LoanDao.class}, (p, method, args) -> {
                    if (fail) throw new SQLException("Test failure");
                    return switch (method.getName()) {
                        case "bookExists" -> exists;
                        case "hasOpenLoan" -> busy;
                        case "countOpenLoans" -> count;
                        case "createLoan" -> { check((int)args[0] == 7 && (int)args[1] == 12, "borrow IDs"); created++; yield 15; }
                        case "findOpenLoans" -> { check((int)args[0] == 7, "member isolation"); yield List.of(new Loan(15, 7, 12, null, LocalDate.now(), null)); }
                        case "markReturned" -> { check((int)args[0] == 15, "return ID"); updated++; yield updateResult; }
                        case "findHistory" -> List.of();
                        default -> throw new AssertionError(method);
                    };
                }));
    }
}
