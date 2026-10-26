package bac.ac.libis.controllers;

import java.util.ArrayList;
import java.util.List;

import bac.ac.libis.data.Seedings;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;



import bac.ac.libis.model.Book;


@WebServlet(name = "catalogue_controller", value = "/catalogue")
public class CatalogueController extends HttpServlet {
    private String message;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/html");
        Member member = (Member) request.getSession().getAttribute("loggedInMember");
        request.setAttribute("loggedInMember", member);
        String search = request.getParameter("search");
        String filterBy = request.getParameter("filterBy");

        List<Book> results;

        if (search == null || search.isBlank()) {
            results = Seedings.books;
        } else {
            results = new ArrayList<>();
            String term = search.trim().toLowerCase();

            for (Book b : Seedings.books) {
                boolean titleMatch = b.getTitle().toLowerCase().contains(term);
                boolean authorMatch = b.getAuthor().toLowerCase().contains(term);
                boolean genreMatch = b.getGenre().toLowerCase().contains(term);

                boolean match;
                if ("title".equals(filterBy)) {
                    match = titleMatch;
                } else if ("author".equals(filterBy)) {
                    match = authorMatch;
                } else if ("genre".equals(filterBy)) {
                    match = genreMatch;
                } else {
                    // "all" or no filter chosen
                    match = titleMatch || authorMatch || genreMatch;
                }

                if (match) {
                    results.add(b);
                }
            }
        }

        request.setAttribute("books", results);
        request.getRequestDispatcher("/WEB-INF/views/Welcome.jsp").forward(request, response);

    }

    }