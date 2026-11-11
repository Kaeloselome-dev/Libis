package bac.ac.libis.controllers;

import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    @Test
    void firstSubmissionUsesRealmThenLoadsProfileForBothRoles() throws Exception {
        for (String role : new String[]{"member", "librarian"}) {
            Flow flow = new Flow();
            flow.role = role;
            flow.controller.doPost(flow.request, flow.response);
            assertEquals(1, flow.logins);
            assertEquals("reader", flow.lookedUp);
            assertSame(flow.member, flow.sessionAttributes.get("loggedInMember"));
            assertEquals("/libis/catalogue", flow.redirect);
            assertEquals(0, flow.status);
        }
    }

    @Test
    void rejectedCredentialsNeverLoadMemberOrRedirect() throws Exception {
        Flow flow = new Flow();
        flow.reject = true;
        flow.sessionAttributes.put("loggedInMember", new Member());
        flow.controller.doPost(flow.request, flow.response);
        assertEquals("/WEB-INF/views/login.jsp", flow.forward);
        assertNull(flow.lookedUp);
        assertNull(flow.redirect);
        assertFalse(flow.sessionAttributes.containsKey("loggedInMember"));
        assertNotNull(flow.attributes.get("errorMessage"));
    }

    @Test
    void missingCredentialsDoNotCallRealm() throws Exception {
        Flow flow = new Flow();
        flow.parameters.clear();
        flow.controller.doPost(flow.request, flow.response);
        assertEquals(0, flow.logins);
        assertEquals("/WEB-INF/views/login.jsp", flow.forward);
    }

    @Test
    void existingPrincipalDoesNotAuthenticateAgain() throws Exception {
        Flow flow = new Flow();
        flow.principal = () -> "reader";
        flow.controller.doGet(flow.request, flow.response);
        assertEquals(0, flow.logins);
        assertSame(flow.member, flow.sessionAttributes.get("loggedInMember"));
        assertEquals("/libis/catalogue", flow.redirect);
    }

    @Test
    void anonymousGetShowsFormWithoutRedirectLoop() throws Exception {
        Flow flow = new Flow();
        flow.controller.doGet(flow.request, flow.response);
        assertEquals("/WEB-INF/views/login.jsp", flow.forward);
        assertNull(flow.redirect);
        assertEquals(0, flow.logins);
    }

    @Test
    void unauthorizedRoleClearsContainerAndApplicationSession() throws Exception {
        Flow flow = new Flow();
        flow.role = "other";
        flow.controller.doPost(flow.request, flow.response);
        assertEquals(403, flow.status);
        assertNull(flow.lookedUp);
        assertNull(flow.principal);
        assertTrue(flow.invalidated);
    }

    @Test
    void missingOrUnavailableProfileRollsBackAuthentication() throws Exception {
        for (boolean databaseFailure : new boolean[]{false, true}) {
            Flow flow = new Flow();
            flow.member = null;
            flow.databaseFailure = databaseFailure;
            flow.controller.doPost(flow.request, flow.response);
            assertEquals(databaseFailure ? 503 : 500, flow.status);
            assertNull(flow.principal);
            assertTrue(flow.invalidated);
            assertNull(flow.redirect);
        }
    }

    private static class Flow {
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> sessionAttributes = new HashMap<>();
        Map<String, String> parameters = new HashMap<>(Map.of(
                "j_username", "reader", "j_password", "test-only-value"));
        Principal principal;
        String role = "member", forward, redirect, lookedUp;
        int logins, status;
        boolean reject, invalidated, databaseFailure;
        Member member = new Member();
        HttpSession session = proxy(HttpSession.class, (method, args) -> {
            switch (method) {
                case "getAttribute": return sessionAttributes.get(args[0]);
                case "setAttribute": sessionAttributes.put((String) args[0], args[1]); break;
                case "removeAttribute": sessionAttributes.remove(args[0]); break;
                case "invalidate": invalidated = true; sessionAttributes.clear(); break;
                default: throw new AssertionError(method);
            }
            return null;
        });
        HttpServletRequest request = proxy(HttpServletRequest.class, (method, args) -> {
            switch (method) {
                case "setCharacterEncoding": return null;
                case "getUserPrincipal": return principal;
                case "getParameter": return parameters.get(args[0]);
                case "getSession": return session;
                case "login":
                    logins++;
                    assertEquals("reader", args[0]);
                    assertNotNull(args[1]);
                    if (reject) throw new ServletException("Rejected");
                    principal = () -> "reader";
                    return null;
                case "logout": principal = null; return null;
                case "isUserInRole": return role.equals(args[0]);
                case "getContextPath": return "/libis";
                case "setAttribute": attributes.put((String) args[0], args[1]); return null;
                case "getRequestDispatcher":
                    forward = (String) args[0];
                    return proxy(RequestDispatcher.class, (name, values) -> null);
                default: throw new AssertionError(method);
            }
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (method, args) -> {
            switch (method) {
                case "sendRedirect": redirect = (String) args[0]; break;
                case "sendError": status = (int) args[0]; break;
                case "setHeader": break;
                default: throw new AssertionError(method);
            }
            return null;
        });
        LoginController controller = new LoginController(new MemberDaoImpl() {
            @Override
            public Member findByUsername(String username) throws SQLException {
                assertNotNull(principal);
                lookedUp = username;
                if (databaseFailure) throw new SQLException("Test failure");
                return member;
            }
        });
    }

    private interface Call {
        Object invoke(String method, Object[] args) throws Throwable;
    }

    private static <T> T proxy(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (instance, method, args) -> call.invoke(method.getName(), args)));
    }
}

