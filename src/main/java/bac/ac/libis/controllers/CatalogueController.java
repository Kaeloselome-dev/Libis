package bac.ac.libis.controllers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import bac.ac.libis.data.Librarydata;
import bac.ac.libis.model.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;



import bac.ac.libis.model.Book;

import jakarta.servlet.http.*;

import jakarta.servlet.annotation.*;



@WebServlet(name = "catalogue_controller", value = "/catalogue")
public class CatalogueController extends HttpServlet {
    private String message;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        Member member = (Member) request.getSession().getAttribute("loggedInMember");
        request.setAttribute("loggedInMember", member);
        String search = request.getParameter("search");

        List<Book> results;

        if (search == null || search.isBlank()) {
            results = Librarydata.books;
        } else {
            results = new ArrayList<>();
            String term = search.trim().toLowerCase();

            for (Book b : Librarydata.books) {
                if (b.getTitle().toLowerCase().contains(term)) {
                    results.add(b);
                }
            }
        }

        request.setAttribute("books", results);
        request.getRequestDispatcher("/WEB-INF/views/welcome.jsp").forward(request, response);

    }

    }