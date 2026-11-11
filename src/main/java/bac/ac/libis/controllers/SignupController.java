package bac.ac.libis.controllers;


import bac.ac.libis.data.Seedings;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import java.sql.SQLException;


import java.io.IOException;


@WebServlet(name = "signup_controller", value = "/signup")
public class SignupController extends HttpServlet {
    private final MemberDao memberDao = new MemberDaoImpl();


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/Signup.jsp").forward(request, response);
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = "member";

        if (username == null || username.isBlank()
                || firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {

            request.setAttribute("errorMessage",
                    "Signup failed. Please fill in all required fields.");

            request.getRequestDispatcher("/WEB-INF/views/Signup.jsp")
                    .forward(request, response);
            return;
        }

        try {
            // The database generates member_id, so use 0 initially.
            Member newMember = new Member(
                    0,
                    username,
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    password,
                    role
            );

            // Temporary member number; see explanation below.
            // Do not use a hardcoded or size-based number.
            // A unique member number must be generated before insertion.

            boolean registered = memberDao.registerMember(newMember);

            if (registered) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                request.setAttribute("errorMessage", "Registration failed.");
                request.getRequestDispatcher("/WEB-INF/views/Signup.jsp")
                        .forward(request, response);
            }

        } catch (SQLException e) {
            log("Failed to register member", e);

            request.setAttribute("errorMessage",
                    "Registration could not be completed.");

            request.getRequestDispatcher("/WEB-INF/views/Signup.jsp")
                    .forward(request, response);
        }
    }
}
