package bac.ac.libis.service;

import bac.ac.libis.dao.BookDao;
import bac.ac.libis.dao.MemberDao;
import bac.ac.libis.exception.ValidationException;
import bac.ac.libis.model.Book;
import bac.ac.libis.model.Member;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.time.Year;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ManagementServiceTest {
    @Test
    void memberRequiresIdentityAndPasswordOnCreation() {
        Members state = new Members();
        Member member = member(0);
        member.setMember_No(""); member.setPassword(""); member.setEmail("bad-email");
        ValidationException error = assertThrows(ValidationException.class, () -> state.service.save(member));
        assertTrue(error.getErrors().keySet().containsAll(List.of("memberNumber", "password", "email")));
        assertEquals(0, state.writes);
    }

    @Test
    void memberDuplicateChecksExcludeTheEditedRecord() throws Exception {
        Members state = new Members();
        state.existing = member(7);
        ValidationException error = assertThrows(ValidationException.class, () -> state.service.save(member(0)));
        assertTrue(error.getErrors().keySet().containsAll(List.of("memberNumber", "username", "email")));
        state.service.save(member(7));
        assertEquals(1, state.writes);
    }

    @Test
    void editingAcceptsBlankPasswordAndPassesItToPasswordPreservingDao() throws Exception {
        Members state = new Members();
        state.existing = member(7);
        Member member = member(7); member.setPassword("");
        state.service.save(member);
        assertEquals("", state.saved.getPassword());
        assertEquals(1, state.writes);
    }

    @Test
    void selfDeleteNeverCallsDeleteAndOtherDeleteIdentifiesMember() throws Exception {
        Members state = new Members(); state.existing = member(7);
        assertThrows(ValidationException.class, () -> state.service.delete(7, 7));
        assertEquals(0, state.writes);
        assertTrue(state.service.delete(7, 9).contains("Test Reader (LIB-7)"));
        assertEquals(1, state.writes);
    }

    @Test
    void bookRejectsMalformedFutureAndOutOfRangeYears() {
        Books state = new Books();
        for (String year : List.of("20x4", "0", "-1", "999999999999999999", "2024.5", String.valueOf(Year.now().getValue() + 1))) {
            ValidationException error = assertThrows(ValidationException.class, () -> state.service.save(book(0), year));
            assertTrue(error.getErrors().containsKey("publicationYear"));
        }
        assertEquals(0, state.writes);
    }

    @Test
    void bookRequiresFieldsAndRejectsDuplicateIsbn() {
        Books state = new Books(); state.existing = book(5);
        Book book = book(0); book.setTitle(""); book.setAuthor("");
        ValidationException error = assertThrows(ValidationException.class, () -> state.service.save(book, "2024"));
        assertTrue(error.getErrors().keySet().containsAll(List.of("title", "author", "isbn")));
        assertEquals(0, state.writes);
    }

    @Test
    void bookCreateAndEditAcceptValidOrUnknownYear() throws Exception {
        Books state = new Books(); Book book = book(0);
        state.service.save(book, "2024"); assertEquals(2024, book.getPublicationYear());
        state.existing = book(5); book = book(5);
        state.service.save(book, ""); assertNull(book.getPublicationYear());
        assertEquals(2, state.writes);
    }

    @Test
    void onLoanBookCannotBeDeletedAndAvailableBookCan() throws Exception {
        Books state = new Books(); state.existing = book(5);
        state.existing.setAvailable(false);
        assertThrows(ValidationException.class, () -> state.service.delete(5));
        assertEquals(0, state.writes);
        state.existing.setAvailable(true);
        assertTrue(state.service.delete(5).contains("Test Book"));
        assertEquals(1, state.writes);
    }

    @Test
    void conditionalDeleteFailureIsReported() {
        Books state = new Books(); state.existing = book(5); state.deleteResult = false;
        assertThrows(ValidationException.class, () -> state.service.delete(5));
    }

    private static Member member(int id) {
        Member member = new Member(id, "reader", "Test", "Reader", "reader@example.com", "", "password", "member");
        member.setMember_No("LIB-7"); return member;
    }
    private static Book book(int id) {
        Book book = new Book(id, "Test Book", "Test Author", "", "", true);
        book.setIsbn("ISBN-1"); return book;
    }
    private interface Call { Object invoke(String name, Object[] args) throws Throwable; }
    private static <T> T proxy(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (p, method, args) -> call.invoke(method.getName(), args)));
    }
    private static class Members {
        Member existing, saved; int writes;
        MemberService service = new MemberService(proxy(MemberDao.class, (name, args) -> switch (name) {
            case "findById", "findByEmail", "findByUsername", "findByMemberNumber" -> existing;
            case "registerMember", "update" -> { saved = (Member) args[0]; writes++; yield true; }
            case "delete" -> { writes++; yield true; }
            default -> throw new AssertionError(name);
        }));
    }
    private static class Books {
        Book existing; int writes; boolean deleteResult = true;
        BookService service = new BookService(proxy(BookDao.class, (name, args) -> switch (name) {
            case "findById", "findByIsbn" -> existing;
            case "create", "update" -> { writes++; yield true; }
            case "delete" -> { writes++; yield deleteResult; }
            default -> throw new AssertionError(name);
        }));
    }
}
