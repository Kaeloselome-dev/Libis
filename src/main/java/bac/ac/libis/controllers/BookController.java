package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;
import bac.ac.libis.model.Book;
import bac.ac.libis.service.BookService;
import bac.ac.libis.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "book_management_controller", urlPatterns = {
        "/librarian/books", "/librarian/books/create", "/librarian/books/edit", "/librarian/books/delete"
})
public class BookController extends HttpServlet {
    private final BookService service;
    private final MemberDao members;
    public BookController() { this(new BookService(), new MemberDaoImpl()); }
    BookController(BookService service, MemberDao members) {
        this.service = service;
        this.members = members;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (librarian(request, response) == null) return;
            String action = action(request);
            if (action.isEmpty()) {
                request.setAttribute("books", service.search(request.getParameter("search"), request.getParameter("filterBy")));
                Object success = request.getSession().getAttribute("managementSuccess");
                request.getSession().removeAttribute("managementSuccess");
                request.setAttribute("successMessage", success);
                view(request, response, "BookList");
            } else if ("create".equals(action)) {
                request.setAttribute("book", new Book());
                view(request, response, "BookForm");
            } else {
                Book record = service.findById(id(request));
                if (record == null) { response.sendError(404, "Book not found."); return; }
                request.setAttribute("book", record);
                request.setAttribute("publicationYear", record.getPublicationYear());
                view(request, response, "delete".equals(action) ? "BookDelete" : "BookForm");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(400, "Please provide a valid book ID.");
        } catch (SQLException e) {
            log("Database error loading books", e);
            response.sendError(503, "Unable to load books. Please try again later.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = action(request);
        if (action.isEmpty()) { response.sendError(405); return; }
        String page = "delete".equals(action) ? "BookDelete" : "BookForm";
        try {
            Member librarian = librarian(request, response);
            if (librarian == null) return;
            int id = "create".equals(action) ? 0 : id(request);
            String success;
            if ("delete".equals(action)) {
                Book record = service.findById(id);
                if (record == null) { response.sendError(404, "Book not found."); return; }
                request.setAttribute("book", record);
                success = service.delete(id);
            } else {
                Book record = readForm(request, id);
                request.setAttribute("book", record);
                request.setAttribute("publicationYear", value(request, "publicationYear"));
                service.save(record, value(request, "publicationYear"));
                success = "Book saved successfully.";
            }
            request.getSession().setAttribute("managementSuccess", success);
            response.sendRedirect(request.getContextPath() + "/librarian/books");
        } catch (ValidationException e) {
            response.setStatus(400);
            request.setAttribute("errors", e.getErrors());
            view(request, response, page);
        } catch (IllegalArgumentException e) {
            response.sendError(400, "Please provide a valid book ID.");
        } catch (SQLException e) {
            log("Database error saving books", e);
            response.setStatus(503);
            request.setAttribute("errorMessage", "Unable to save changes. Please try again later.");
            view(request, response, page);
        }
    }

    private Member librarian(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        response.setHeader("Cache-Control", "no-store");
        if (!request.isUserInRole("librarian")) { response.sendError(403); return null; }
        Member librarian = members.findByUsername(request.getRemoteUser());
        if (librarian == null) { response.sendError(403, "Unable to identify your librarian account."); return null; }
        request.setAttribute("loggedInMember", librarian);
        return librarian;
    }

    private String action(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/librarian/books") ? "" : path.substring(path.lastIndexOf('/') + 1);
    }
    private int id(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        if (id <= 0) throw new IllegalArgumentException();
        return id;
    }
    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
    private Book readForm(HttpServletRequest request, int id) {
        Book book = new Book();
        book.setBookId(id);
        book.setIsbn(value(request, "isbn"));
        book.setTitle(value(request, "title"));
        book.setAuthor(value(request, "author"));
        book.setGenre(value(request, "genre"));
        book.setSynopsis(value(request, "synopsis"));
        return book;
    }
    private void view(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(request, response);
    }
}
