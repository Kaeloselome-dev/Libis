package bac.ac.libis;

import bac.ac.libis.data.LibraryData;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "searchServlet", value = "/search-servlet")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("query");
        String filterBy = request.getParameter("filterBy");
        List<Member> searchResults = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();

            for (Member mem : LibraryData.members) {
                boolean match = false;

                if ("email".equalsIgnoreCase(filterBy)) {
                    match = mem.getEmail() != null && mem.getEmail().toLowerCase().contains(q);
                } else if ("id".equalsIgnoreCase(filterBy)) {
                    match = String.valueOf(mem.getMemberId()).contains(q);
                } else if ("name".equalsIgnoreCase(filterBy)) {
                    match = (mem.getFirstName() != null && mem.getFirstName().toLowerCase().contains(q))
                            || (mem.getLastName() != null && mem.getLastName().toLowerCase().contains(q));
                } else {
                    // "all" or default: check all fields
                    match = (mem.getEmail() != null && mem.getEmail().toLowerCase().contains(q))
                            || String.valueOf(mem.getMemberId()).contains(q)
                            || (mem.getFirstName() != null && mem.getFirstName().toLowerCase().contains(q))
                            || (mem.getLastName() != null && mem.getLastName().toLowerCase().contains(q));
                }

                if (match) {
                    searchResults.add(mem);
                }
            }
        }

        request.setAttribute("searchResults", searchResults);
        request.getRequestDispatcher("/Search.jsp").forward(request, response);
    }
}