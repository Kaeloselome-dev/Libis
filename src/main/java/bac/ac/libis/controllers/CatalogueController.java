
package bac.ac.libis.controllers;

import bac.ac.libis.dao.BookDao;
import bac.ac.libis.model.Book;

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

    private final BookDao bookDAO = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException {

        response.setContentType("text/html;charset=UTF-8");

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
