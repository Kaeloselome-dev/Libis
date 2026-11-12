package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.exception.ValidationException;
import bac.ac.libis.model.Book;
import bac.ac.libis.model.Member;
import bac.ac.libis.service.BookService;
import bac.ac.libis.service.MemberService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ManagementControllerTest {
    @Test
    void ordinaryMembersAreRejectedBeforeAnyLookupOrWrite() throws Exception {
        for (boolean members : List.of(true, false)) {
            Flow flow = new Flow(members); flow.librarian = false;
            flow.get(); assertEquals(403, flow.status); assertNull(flow.lookedUp);
            flow.path += "/delete"; flow.post();
            assertEquals(403, flow.status); assertEquals(0, flow.writes);
        }
    }

    @Test
    void getDeleteOnlyDisplaysConfirmationAndPostRedirects() throws Exception {
        for (boolean members : List.of(true, false)) {
            Flow flow = new Flow(members); flow.path += "/delete"; flow.params.put("id", "7");
            flow.get();
            assertTrue(flow.forward.endsWith("Delete.jsp")); assertEquals(0, flow.writes);
            assertEquals("librarian", flow.lookedUp);
            flow.post();
            assertEquals(1, flow.writes); assertEquals("/libis/" + flow.base, flow.redirect);
            assertNotNull(flow.sessionValues.get("managementSuccess"));
        }
    }

    @Test
    void invalidCreateForwardsEnteredValuesAndSpecificErrors() throws Exception {
        for (boolean members : List.of(true, false)) {
            Flow flow = new Flow(members); flow.path += "/create"; flow.reject = true;
            flow.params.put("firstName", "Entered Name"); flow.params.put("title", "Entered Title");
            flow.params.put("publicationYear", "20x4");
            flow.post();
            assertEquals(400, flow.status); assertNull(flow.redirect);
            assertTrue(flow.forward.endsWith("Form.jsp"));
            assertEquals(Map.of("form", "Specific validation error"), flow.attributes.get("errors"));
            if (members) assertEquals("Entered Name", ((Member) flow.attributes.get("member")).getFirstName());
            else {
                assertEquals("Entered Title", ((Book) flow.attributes.get("book")).getTitle());
                assertEquals("20x4", flow.attributes.get("publicationYear"));
            }
        }
    }

    @Test
    void createAndEditUsePostRedirectGet() throws Exception {
        for (boolean members : List.of(true, false)) for (String action : List.of("create", "edit")) {
            Flow flow = new Flow(members); flow.path += "/" + action; flow.params.put("id", "7");
            flow.post(); assertEquals(1, flow.writes); assertEquals("/libis/" + flow.base, flow.redirect);
        }
    }

    @Test
    void listPassesSearchParametersAndConsumesSuccessMessage() throws Exception {
        for (boolean members : List.of(true, false)) {
            Flow flow = new Flow(members); flow.params.put("search", "Test"); flow.params.put("filterBy", "name");
            flow.sessionValues.put("managementSuccess", "Saved"); flow.get();
            assertEquals("Test", flow.search); assertEquals("name", flow.filter);
            assertEquals("Saved", flow.attributes.get("successMessage"));
            assertFalse(flow.sessionValues.containsKey("managementSuccess"));
            assertTrue(flow.forward.endsWith("List.jsp"));
        }
    }

    private interface Call { Object invoke(String name, Object[] args) throws Throwable; }
    private static <T> T proxy(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (p, method, args) -> call.invoke(method.getName(), args)));
    }
    private static class Flow {
        final boolean memberManagement;
        final String base;
        String path, forward, redirect, lookedUp, search, filter;
        boolean librarian = true, reject;
        int status, writes;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>(), sessionValues = new HashMap<>();
        HttpSession session = proxy(HttpSession.class, (name, args) -> switch (name) {
            case "getAttribute" -> sessionValues.get(args[0]);
            case "setAttribute" -> { sessionValues.put((String) args[0], args[1]); yield null; }
            case "removeAttribute" -> sessionValues.remove(args[0]);
            default -> throw new AssertionError(name);
        });
        HttpServletRequest request = proxy(HttpServletRequest.class, (name, args) -> switch (name) {
            case "isUserInRole" -> librarian;
            case "getRemoteUser" -> "librarian";
            case "getServletPath" -> path;
            case "getContextPath" -> "/libis";
            case "getParameter" -> params.get(args[0]);
            case "getSession" -> session;
            case "setCharacterEncoding" -> null;
            case "setAttribute" -> { attributes.put((String) args[0], args[1]); yield null; }
            case "getRequestDispatcher" -> {
                forward = (String) args[0]; yield proxy(RequestDispatcher.class, (method, values) -> null);
            }
            default -> throw new AssertionError(name);
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (name, args) -> switch (name) {
            case "setHeader" -> null;
            case "sendError", "setStatus" -> { status = (int) args[0]; yield null; }
            case "sendRedirect" -> { redirect = (String) args[0]; yield null; }
            default -> throw new AssertionError(name);
        });
        MemberDao dao = proxy(MemberDao.class, (name, args) -> {
            lookedUp = (String) args[0]; Member librarian = new Member(); librarian.setMemberId(9); return librarian;
        });
        HttpServlet controller;
        Flow(boolean members) {
            memberManagement = members; base = "librarian/" + (members ? "members" : "books"); path = "/" + base;
            controller = members ? new MemberController(new MemberService() {
                public List<Member> search(String term, String field) { search = term; filter = field; return List.of(); }
                public Member findById(int id) { Member member = new Member(); member.setMemberId(id); return member; }
                public void save(Member member) throws ValidationException { save(); }
                private void save() throws ValidationException { if (reject) throw error(); writes++; }
                public String delete(int id, int librarianId) { assertEquals(9, librarianId); writes++; return "Deleted member"; }
            }, dao) : new BookController(new BookService() {
                public List<Book> search(String term, String field) { search = term; filter = field; return List.of(); }
                public Book findById(int id) { Book book = new Book(); book.setBookId(id); return book; }
                public void save(Book book, String year) throws ValidationException { if (reject) throw error(); writes++; }
                public String delete(int id) { writes++; return "Deleted book"; }
            }, dao);
        }
        ValidationException error() { return new ValidationException(Map.of("form", "Specific validation error")); }
        void get() throws Exception {
            if (memberManagement) ((MemberController) controller).doGet(request, response);
            else ((BookController) controller).doGet(request, response);
        }
        void post() throws Exception {
            if (memberManagement) ((MemberController) controller).doPost(request, response);
            else ((BookController) controller).doPost(request, response);
        }
    }
}
