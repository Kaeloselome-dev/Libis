package bac.ac.libis.utility;

import bac.ac.libis.config.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/dbtest")
public class DbTestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain;charset=UTF-8");

        try (Connection con = DatabaseConfig.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM members")) {

            rs.next();
            resp.getWriter().println("Connected. Members in database: " + rs.getInt(1));

        } catch (SQLException e) {
            resp.getWriter().println("FAILED: " + e.getMessage());
            if (e.getCause() != null) {
                resp.getWriter().println("Cause: " + e.getCause());
            }
        }
    }
}