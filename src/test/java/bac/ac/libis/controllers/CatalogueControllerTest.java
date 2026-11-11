package bac.ac.libis.controllers;

import bac.ac.libis.dao.BookDao;
import bac.ac.libis.dao.MemberDaoImpl;
import bac.ac.libis.model.Member;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatalogueControllerTest {
    @Test
    void containerLoginLoadsMemberWithoutAnApplicationSession() throws Exception {
        Flow flow = new Flow();
        flow.controller.doGet(flow.request, flow.response);
        assertEquals("reader", flow.lookedUp);
        assertSame(flow.member, flow.attributes.get("loggedInMember"));
        assertEquals("/WEB-INF/views/Welcome.jsp", flow.forward);
        assertEquals(List.of(), flow.attributes.get("books"));
    }

    @Test
    void missingProfileDoesNotRenderWelcomePage() throws Exception {
        Flow flow = new Flow();
        flow.member = null;
        flow.controller.doGet(flow.request, flow.response);
        assertEquals(500, flow.status);
        assertNull(flow.forward);
        assertFalse(flow.attributes.containsKey("loggedInMember"));
    }

    @Test
    void anonymousRequestReturnsToLoginWithoutLookingUpMember() throws Exception {
        Flow flow = new Flow();
        flow.username = null;
        flow.controller.doGet(flow.request, flow.response);
        assertEquals("/libis/login", flow.redirect);
        assertNull(flow.lookedUp);
        assertNull(flow.forward);
    }

    private static class Flow {
        String username = "reader", lookedUp, forward, redirect;
        int status;
        Member member = new Member();
        Map<String, Object> attributes = new HashMap<>();
        HttpServletRequest request = proxy(HttpServletRequest.class, (method, args) -> {
            switch (method) {
                case "getRemoteUser": return username;
                case "getContextPath": return "/libis";
                case "getParameter": return null;
                case "setAttribute": attributes.put((String) args[0], args[1]); return null;
                case "getRequestDispatcher":
                    forward = (String) args[0];
                    return proxy(RequestDispatcher.class, (name, values) -> null);
                default: throw new AssertionError(method);
            }
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (method, args) -> {
            switch (method) {
                case "setContentType": break;
                case "sendError": status = (int) args[0]; break;
                case "sendRedirect": redirect = (String) args[0]; break;
                default: throw new AssertionError(method);
            }
            return null;
        });
        CatalogueController controller = new CatalogueController(
                proxy(BookDao.class, (method, args) -> List.of()),
                new MemberDaoImpl() {
                    @Override
                    public Member findByUsername(String username) {
                        lookedUp = username;
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
