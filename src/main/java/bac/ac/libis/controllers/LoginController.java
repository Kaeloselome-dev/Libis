package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "login_controller", value = "/login")
public class LoginController extends HttpServlet {
    private final MemberDao memberDao;

    public LoginController() {
        this(new MemberDaoImpl());
    }

    LoginController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getUserPrincipal() != null) {
            completeLogin(request, response);
            return;
        }
        showLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (request.getUserPrincipal() == null) {
            String username = request.getParameter("j_username");
            String password = request.getParameter("j_password");
            if (username == null || username.isBlank() || password == null || password.isEmpty()) {
                request.setAttribute("errorMessage", "Please enter your username and password.");
                showLogin(request, response);
                return;
            }
            // Tomcat caches the principal and rotates the existing session ID on login.
            request.getSession().removeAttribute("loggedInMember");
            try {
                // Only the configured Realm verifies credentials and assigns roles.
                request.login(username, password);
            } catch (ServletException e) {
                request.setAttribute("errorMessage", "Invalid username or password.");
                showLogin(request, response);
                return;
            }
        }
        completeLogin(request, response);
    }

    private void completeLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!request.isUserInRole("member") && !request.isUserInRole("librarian")) {
            clearLogin(request);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            Member member = memberDao.findByUsername(request.getUserPrincipal().getName());
            if (member == null) {
                clearLogin(request);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to load your member profile. Please try again later.");
                return;
            }
            request.getSession().setAttribute("loggedInMember", member);
            response.sendRedirect(request.getContextPath() + "/catalogue");
        } catch (SQLException e) {
            // Never leave an authenticated session without its member profile.
            clearLogin(request);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Unable to load your member profile. Please try again later.");
        }
    }

    private void clearLogin(HttpServletRequest request) throws ServletException {
        request.logout();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}

