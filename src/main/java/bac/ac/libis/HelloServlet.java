/*package bac.ac.libis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bac.ac.libis.data.Librarydata;
import bac.ac.libis.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import static org.apache.taglibs.standard.functions.Functions.containsIgnoreCase;

@WebServlet(name = "helloServlet", value = "/login-servlet")
public class HelloServlet extends HttpServlet {
    private String message;


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
            for (Member m : Librarydata.members) {
                if (email.equalsIgnoreCase(m.getEmail()) && password.equals(m.getPassword())) {
                    authenticatedMember = m;
                    break;
                }
            }
        }

        if (authenticatedMember != null) {
            request.getSession().setAttribute("loggedInMember", authenticatedMember);

            request.setAttribute("loggedInMember", authenticatedMember);
            request.setAttribute("books", Librarydata.books);
            request.getRequestDispatcher("/Welcome.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected void Signup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (firstName != null && !firstName.isBlank() && email != null && !email.isBlank()) {
            Member newMember = new Member(Librarydata.members.size() + 1001, firstName, lastName, email, phoneNumber, password);
            Librarydata.members.add(newMember);

            // Set session and request attributes BEFORE forwarding
            request.getSession().setAttribute("loggedInMember", newMember);
            request.setAttribute("loggedInMember", newMember);
            request.setAttribute("books",Librarydata.books);

            request.getRequestDispatcher("/Welcome.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Signup failed. Please fill in all required fields.");
            request.getRequestDispatcher("/Signup.jsp").forward(request, response);
        }
    }


    protected void Serach(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("query");
        String filterBy = request.getParameter("filterBy");

        String q = query == null ? "" : query.trim().toLowerCase();

        List<Member> memberResults = new ArrayList<>();
        List<Book> bookResults = new ArrayList<>();

        if (!q.isEmpty()) {
            for (Member mem : Librarydata.members) {
                boolean match;

                if ("email".equalsIgnoreCase(filterBy)) {
                    match = containsIgnoreCase(mem.getEmail(), q);
                } else if ("id".equalsIgnoreCase(filterBy)) {
                    match = String.valueOf(mem.getMemberId()).contains(q);
                } else if ("name".equalsIgnoreCase(filterBy)) {
                    match = containsIgnoreCase(mem.getFirstName(), q)
                            || containsIgnoreCase(mem.getLastName(), q);
                } else if (isMemberFilter(filterBy)) {
                    match = false;
                } else {
                    match = containsIgnoreCase(mem.getEmail(), q)
                            || String.valueOf(mem.getMemberId()).contains(q)
                            || containsIgnoreCase(mem.getFirstName(), q)
                            || containsIgnoreCase(mem.getLastName(), q);
                }

                if (match) {
                    memberResults.add(mem);
                }
            }

            /*for (Book bk : books) {
                boolean match;

                if ("title".equalsIgnoreCase(filterBy)) {
                    match = containsIgnoreCase(bk.getTitle(), q);
                } else if ("author".equalsIgnoreCase(filterBy)) {
                    match = containsIgnoreCase(bk.getAuthor(), q);
                } else if ("genre".equalsIgnoreCase(filterBy)) {
                    match = containsIgnoreCase(bk.getGenre(), q);
                } else if (isMemberFilter(filterBy)) {
                    match = false;
                } else {
                    match = containsIgnoreCase(bk.getTitle(), q)
                            || containsIgnoreCase(bk.getAuthor(), q)
                            || containsIgnoreCase(bk.getGenre(), q);
                }

                if (match) {
                    bookResults.add(bk);
                }
            }
        }*

        request.setAttribute("memberResults", memberResults);
        request.setAttribute("bookResults", bookResults);
        request.getRequestDispatcher("/Search.jsp").forward(request, response);
    }


    private static boolean isMemberFilter(String filterBy) {
        return "name".equalsIgnoreCase(filterBy)
                || "id".equalsIgnoreCase(filterBy)
                || "email".equalsIgnoreCase(filterBy);
    }

    private static boolean isBookFilter(String filterBy) {
        return "title".equalsIgnoreCase(filterBy)
                || "author".equalsIgnoreCase(filterBy)
                || "genre".equalsIgnoreCase(filterBy);
    }




    public void destroy() {
    }
}*/