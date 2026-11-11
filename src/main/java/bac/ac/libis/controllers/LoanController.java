package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;
import bac.ac.libis.service.LoanService;
import bac.ac.libis.service.LoanException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "loan_controller", urlPatterns = {
        "/member/loans", "/member/loans/borrow", "/member/loans/return"
})
public class LoanController extends HttpServlet {
    private final MemberDao members;
    private final LoanService loans;

    public LoanController() {
        this(new MemberDaoImpl(), new LoanService());
    }

    LoanController(MemberDao members, LoanService loans) {
        this.members = members;
        this.loans = loans;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/member/loans".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        try {
            Member member = resolveMember(request, response);
            if (member == null) return;
            request.setAttribute("openLoans", loans.findOpenLoans(member.getMemberId()));
            request.setAttribute("loanHistory", loans.findHistory(member.getMemberId()));
            response.setHeader("Cache-Control", "no-store");
            request.getRequestDispatcher("/WEB-INF/views/MyLoans.jsp").forward(request, response);
        } catch (SQLException e) {
            databaseError(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        if (!"/member/loans/borrow".equals(path) && !"/member/loans/return".equals(path)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        try {
            Member member = resolveMember(request, response);
            if (member == null) return;
            if ("/member/loans/borrow".equals(path)) {
                loans.borrow(member.getMemberId(), positiveId(request.getParameter("bookId")));
            } else {
                loans.returnLoan(member.getMemberId(), positiveId(request.getParameter("loanId")));
            }
            response.sendRedirect(request.getContextPath() + "/member/loans");
        } catch (LoanException | IllegalArgumentException e) {
            showError(request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            databaseError(request, response, e);
        }
    }

    private Member resolveMember(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String username = request.getRemoteUser();
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        Member member = members.findByUsername(username);
        if (member == null) {
            showError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load your member profile. Please try again later.");
            return null;
        }
        request.setAttribute("loggedInMember", member);
        return member;
    }

    private int positiveId(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) return id;
        } catch (NumberFormatException ignored) {
            // Convert missing, malformed, and out-of-range IDs into a user-facing error.
        }
        throw new IllegalArgumentException("Please provide a valid book or loan ID.");
    }

    private void databaseError(HttpServletRequest request, HttpServletResponse response, SQLException e)
            throws ServletException, IOException {
        getServletContext().log("Database error processing loan request", e);
        showError(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                "Unable to process your loan request. Please try again later.");
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, int status, String message)
            throws ServletException, IOException {
        response.setStatus(status);
        response.setHeader("Cache-Control", "no-store");
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/WEB-INF/views/LoanError.jsp").forward(request, response);
    }
}

