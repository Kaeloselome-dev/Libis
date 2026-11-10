
/*
package bac.ac.libis.filter;

import java.io.IOException;

import bac.ac.libis.model.Member;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();

        if (path.equals("/") || path.equals("/index.jsp")
                || path.equals("/login") || path.equals("/signup")
                || path.endsWith(".css")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Member member = (session != null) ? (Member) session.getAttribute("loggedInMember") : null;

        if (member != null) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
*/