
package bac.ac.libis.controllers;

import bac.ac.libis.dao.BookDao;
import bac.ac.libis.dao.BookDaoimpl;
import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Book;
import bac.ac.libis.model.Member;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "catalogue_controller", value = "/catalogue")
public class CatalogueController extends HttpServlet {

    private final BookDao bookDAO;
    private final MemberDao memberDao;

    public CatalogueController() {
        this(new BookDaoimpl(), new MemberDaoImpl());
    }

    CatalogueController(BookDao bookDAO, MemberDao memberDao) {
        this.bookDAO = bookDAO;
        this.memberDao = memberDao;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException {

        response.setContentType("text/html;charset=UTF-8");

        String username = request.getRemoteUser();
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Member member = memberDao.findByUsername(username);
            if (member == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to load your member profile. Please try again later.");
                return;
            }
            request.setAttribute("loggedInMember", member);
        } catch (SQLException e) {
            getServletContext().log("Database error loading member profile", e);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Unable to load your member profile. Please try again later.");
            return;
        }

        try {
            // Retrieve catalogue search parameters
            String search = request.getParameter("search");
            String filterBy = request.getParameter("filterBy");

            List<Book> results;

            if (search == null || search.isBlank()) {
                results = bookDAO.findAll();
            } else {
                results = bookDAO.search(search, filterBy);
            }

            // Forward results to JSP
            request.setAttribute("books", results);

        } catch (SQLException e) {

            getServletContext().log(
                    "Database error loading catalogue", e
            );

            request.setAttribute(
                    "error",
                    "Unable to load the catalogue. Please try again later."
            );

            request.setAttribute("books", new ArrayList<Book>());
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/Welcome.jsp"
        ).forward(request, response);
    }
}
