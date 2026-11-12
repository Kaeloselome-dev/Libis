package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;
import bac.ac.libis.model.Loan;
import bac.ac.libis.service.LoanService;
import bac.ac.libis.exception.LoanException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Proxy;
import java.util.*;

/** Standalone HTTP delegation checks; run main with the servlet API on the classpath. */
public class LoanControllerFlowCheck {
    public static void main(String[] args) throws Exception {
        Flow f = new Flow();
        f.controller.doPost(f.request, f.response);
        check(f.memberId == 7 && f.targetId == 12, "borrow delegation");
        check("/libis/member/loans".equals(f.redirect), "success redirect");
        f = new Flow(); f.path = "/member/loans/return";
        f.controller.doPost(f.request, f.response);
        check(f.returned && f.memberId == 7, "return delegation");
        f = new Flow(); f.path = "/member/loans";
        f.controller.doGet(f.request, f.response);
        check(f.attributes.containsKey("openLoans") && f.attributes.containsKey("loanHistory"), "page data");
        check(Integer.valueOf(LoanService.MAX_LOANS).equals(f.attributes.get("maxLoans")), "service limit exposed");
        f = new Flow(); f.reject = true;
        f.controller.doPost(f.request, f.response);
        check(f.status == 400 && "Service rejected request".equals(f.attributes.get("errorMessage")), "service error");
        check("/WEB-INF/views/MyLoans.jsp".equals(f.forwarded) && f.attributes.containsKey("openLoans") && f.attributes.containsKey("loanHistory"), "rejection redisplays loans page");
        f = new Flow(); f.username = null;
        f.controller.doPost(f.request, f.response);
        check(f.memberId == 0 && "/libis/login".equals(f.redirect), "anonymous request");
        f = new Flow(); f.missing = true;
        f.controller.doPost(f.request, f.response);
        check(f.status == 500 && f.memberId == 0, "missing member");
        f = new Flow(); f.id = "invalid";
        f.controller.doPost(f.request, f.response);
        check(f.status == 400 && f.memberId == 0, "invalid input");
        System.out.println("Passed 7 LoanController flow checks.");
    }
    static void check(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    static class Flow {
        String forwarded;
        String username = "reader", path = "/member/loans/borrow", id = "12", redirect;
        int memberId, targetId, status;
        boolean returned, reject, missing;
        Map<String,Object> attributes = new HashMap<>();
        HttpServletRequest request = proxy(HttpServletRequest.class, (method, a) -> switch (method) {
            case "getRemoteUser" -> username;
            case "getServletPath" -> path;
            case "getContextPath" -> "/libis";
            case "getParameter" -> id;
            case "setCharacterEncoding" -> null;
            case "getAttribute" -> attributes.get((String)a[0]);
            case "setAttribute" -> { attributes.put((String)a[0], a[1]); yield null; }
            case "getRequestDispatcher" -> { forwarded = (String)a[0]; yield proxy(RequestDispatcher.class, (name, values) -> null); }
            default -> throw new AssertionError(method);
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (method, a) -> {
            if ("encodeRedirectURL".equals(method)) return a[0];
            switch(method) {
                case "sendRedirect": redirect = (String)a[0]; break;
                case "setStatus": case "sendError": status = (int)a[0]; break;
                case "setHeader": break;
                default: throw new AssertionError(method);
            }
            return null;
        });
        LoanController controller = new LoanController(new MemberDaoImpl() {
            public Member findByUsername(String username) {
                check("reader".equals(username), "authenticated username lookup");
                if (missing) return null;
                Member m = new Member(); m.setMemberId(7); return m;
            }
        }, new LoanService() {
            public void borrow(int member, int book) throws LoanException {
                if (reject) throw new LoanException("Service rejected request");
                memberId = member; targetId = book;
            }
            public void returnLoan(int member, int loan) { memberId = member; targetId = loan; returned = true; }
            public List<Loan> findOpenLoans(int member) { return List.of(); }
            public List<Loan> findHistory(int member) { return List.of(); }
        });
    }
    interface Call { Object invoke(String method, Object[] args) throws Throwable; }
    static <T> T proxy(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (p, m, a) -> call.invoke(m.getName(), a)));
    }
}

