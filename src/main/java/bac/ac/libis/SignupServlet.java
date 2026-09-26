package bac.ac.libis;

import bac.ac.libis.data.LibraryData;
import bac.ac.libis.model.Book;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "SignupServlet", value = "/signup-servlet")
public class SignupServlet extends HttpServlet {

    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();

    @Override
    public void init() {
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

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (firstName != null && !firstName.isBlank() && email != null && !email.isBlank()) {
            Member newMember = new Member(members.size() + 1001, firstName, lastName, email, phoneNumber, password);
            members.add(newMember);

            // Set session and request attributes BEFORE forwarding
            request.getSession().setAttribute("loggedInMember", newMember);
            request.setAttribute("loggedInMember", newMember);
            request.setAttribute("books", LibraryData.books);

            request.getRequestDispatcher("/Welcome.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Signup failed. Please fill in all required fields.");
            request.getRequestDispatcher("/Signup.jsp").forward(request, response);
        }
    }
}