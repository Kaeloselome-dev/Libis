package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;

import bac.ac.libis.service.MemberService;
import bac.ac.libis.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "member_management_controller", urlPatterns = {
        "/librarian/members", "/librarian/members/create", "/librarian/members/edit", "/librarian/members/delete"
})
public class MemberController extends HttpServlet {
    private final MemberService service;
    private final MemberDao members;
    public MemberController() { this(new MemberService(), new MemberDaoImpl()); }
    MemberController(MemberService service, MemberDao members) {
        this.service = service;
        this.members = members;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (librarian(request, response) == null) return;
            String action = action(request);
            if (action.isEmpty()) {
                request.setAttribute("members", service.search(request.getParameter("search"), request.getParameter("filterBy")));
                Object success = request.getSession().getAttribute("managementSuccess");
                request.getSession().removeAttribute("managementSuccess");
                request.setAttribute("successMessage", success);
                view(request, response, "MemberList");
            } else if ("create".equals(action)) {
                request.setAttribute("member", new Member());
                view(request, response, "MemberForm");
            } else {
                Member record = service.findById(id(request));
                if (record == null) { response.sendError(404, "Member not found."); return; }
                request.setAttribute("member", record);
                
                view(request, response, "delete".equals(action) ? "MemberDelete" : "MemberForm");
            }
        } catch (IllegalArgumentException e) {
            response.sendError(400, "Please provide a valid member ID.");
        } catch (SQLException e) {
            log("Database error loading members", e);
            response.sendError(503, "Unable to load members. Please try again later.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = action(request);
        if (action.isEmpty()) { response.sendError(405); return; }
        String page = "delete".equals(action) ? "MemberDelete" : "MemberForm";
        try {
            Member librarian = librarian(request, response);
            if (librarian == null) return;
            int id = "create".equals(action) ? 0 : id(request);
            String success;
            if ("delete".equals(action)) {
                Member record = service.findById(id);
                if (record == null) { response.sendError(404, "Member not found."); return; }
                request.setAttribute("member", record);
                success = service.delete(id, librarian.getMemberId());
            } else {
                Member record = readForm(request, id);
                request.setAttribute("member", record);
                
                service.save(record);
                success = "Member saved successfully.";
            }
            request.getSession().setAttribute("managementSuccess", success);
            response.sendRedirect(request.getContextPath() + "/librarian/members");
        } catch (ValidationException e) {
            response.setStatus(400);
            request.setAttribute("errors", e.getErrors());
            view(request, response, page);
        } catch (IllegalArgumentException e) {
            response.sendError(400, "Please provide a valid member ID.");
        } catch (SQLException e) {
            log("Database error saving members", e);
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
        return path.equals("/librarian/members") ? "" : path.substring(path.lastIndexOf('/') + 1);
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
    private Member readForm(HttpServletRequest request, int id) {
        Member member = new Member();
        member.setMemberId(id);
        member.setMember_No(value(request, "memberNumber"));
        member.setFirstName(value(request, "firstName"));
        member.setLastName(value(request, "lastName"));
        member.setUsername(value(request, "username"));
        member.setEmail(value(request, "email"));
        member.setPhoneNumber(value(request, "phoneNumber"));
        member.setPassword(request.getParameter("password") == null ? "" : request.getParameter("password"));
        return member;
    }
    private void view(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(request, response);
    }
}
