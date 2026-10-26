package bac.ac.libis.controllers;

import bac.ac.libis.data.Seedings;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet(name = "login_controller", value = "/login")
public class LoginController extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Member authenticatedMember = null;

        if (email != null && password != null) {
            for (Member m : Seedings.members) {
                if (email.equalsIgnoreCase(m.getEmail()) && password.equals(m.getPassword())) {
                    authenticatedMember = m;
                    break;
                }
            }
        }

        if (authenticatedMember != null) {
            request.getSession().setAttribute("loggedInMember", authenticatedMember);
            response.sendRedirect(request.getContextPath() + "/catalogue");
        } else {
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
