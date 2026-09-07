package bac.ac.libis;

import java.io.*;
import java.util.ArrayList;

import bac.ac.libis.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/login-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    ArrayList<Member> members = new ArrayList<Member>();
    ArrayList<Book> books = new ArrayList<Book>();

    public void init() {

        Member user1 = new Member(1001, "Kaelo", "Selome", "Kaelo@gmail.com", "Kaelo12345");
        Member user2 = new Member(1002, "Tom", "Doe", "Tom@gmail.com", "password");
        Member user3 = new Member(1003, "John", "Doe", "John@gmail.com", "password");
        Member user4 = new Member(1004, "Bob", "Doe", "Bob@gmail.com", "password");
        Member user5 = new Member(1005, "Rasta", "Doe", "Rasta@gmail.com", "password");


        members.add(user1);
        members.add(user2);
        members.add(user3);
        members.add(user4);
        members.add(user5);

        Book book1 = new Book(1001, "48 Laws of Power", "Ene oo", "Educational", "What", true);
        Book book2 = new Book(1002, "1984", "George Orwell", "Educational", "What", true);
        Book book3 = new Book(1001, "The Catcher in the Rye", "J.D. Salinger", "Fiction", "What", true);
        Book book4 = new Book(1001, "The Great Gatsby", "Scott Fitzgerald", "Classic", "What", true);
        Book book5 = new Book(1001, "The Hobbit", "J.R.R. Tolkien", "Fiction", "What", true);
        Book book6 = new Book(1001, "Fahrenheit 451", "Ray Bradbury", "Educational", "What", true);
        Book book7 = new Book(1001, "The Alchemist", "Ene oo", "Educational", "What", true);
        Book book8 = new Book(1001, "Brave New World", "Aldous Huxley", "Fiction", "What", true);
        Book book9 = new Book(1001, "Clean Code", "Robert C. Martin", "Software Engineering", "What", true);
        Book book10 = new Book(1001, "To Kill a Mockingbird", "Harper Lee", "Educational", "What", true);


        books.add(book1);
        books.add(book2);
        books.add(book3);
        books.add(book4);
        books.add(book5);
        books.add(book6);
        books.add(book7);
        books.add(book8);
        books.add(book9);
        books.add(book10);
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
            // Store in session if you want it to persist across multiple pages
            request.getSession().setAttribute("loggedInMember", authenticatedMember);

            request.setAttribute("loggedInMember", authenticatedMember);
            request.setAttribute("books", books);
            request.getRequestDispatcher("/Welcome.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
        }
    }




    public void destroy() {
    }
}