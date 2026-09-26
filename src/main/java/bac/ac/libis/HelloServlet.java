package bac.ac.libis;

import java.io.*;
import java.util.ArrayList;

import bac.ac.libis.data.LibraryData;
import bac.ac.libis.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import static bac.ac.libis.data.LibraryData.members;

@WebServlet(name = "helloServlet", value = "/login-servlet")
public class HelloServlet extends HttpServlet {
    private String message;


    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Member authenticatedMember = null;

        if (email != null && password != null) {
            for (Member m : members) {
                if (email.equalsIgnoreCase(m.getEmail()) && password.equals(m.getPassword())) {
                    authenticatedMember = m;
                    break;
                }
            }
        }

        if (authenticatedMember != null) {
            request.getSession().setAttribute("loggedInMember", authenticatedMember);

            request.setAttribute("loggedInMember", authenticatedMember);
            request.setAttribute("books", LibraryData.books);
            request.getRequestDispatcher("/Welcome.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
        }
    }




    public void destroy() {
    }
}