package bac.ac.libis.controllers;

import bac.ac.libis.data.Seedings;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet(name = "signup_controller", value = "/signup")
public class SignupController extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/Signup.jsp").forward(request, response);
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (firstName != null && !firstName.isBlank() && email != null && !email.isBlank()) {
            Member newMember = new Member(Seedings.members.size() + 1001, firstName, lastName, email, phoneNumber, password);
            Seedings.members.add(newMember);

            request.getSession().setAttribute("loggedInMember", newMember);
            response.sendRedirect(request.getContextPath() + "/catalogue");
        } else {
            request.setAttribute("errorMessage", "Signup failed. Please fill in all required fields.");
            request.getRequestDispatcher("/WEB-INF/views/Signup.jsp").forward(request, response);
        }
    }
}
