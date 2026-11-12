package bac.ac.libis.dao;

import bac.ac.libis.model.Book;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import static org.junit.jupiter.api.Assertions.*;

class BookDaoMappingTest {
    @Test
    void originalSchemaStillLoadsBooksWithoutPublicationYear() throws Exception {
        Book book = map(false, null);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertTrue(book.isAvailable());
        assertNull(book.getPublicationYear());
    }

    @Test
    void migratedSchemaLoadsKnownAndUnknownYears() throws Exception {
        assertEquals(2024, map(true, 2024).getPublicationYear());
        assertNull(map(true, null).getPublicationYear());
    }

    private Book map(boolean hasYear, Integer year) throws Exception {
        ResultSetMetaData metadata = (ResultSetMetaData) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{ResultSetMetaData.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getColumnCount" -> hasYear ? 2 : 1;
                    case "getColumnLabel" -> (int) args[0] == 1 ? "title" : "publication_year";
                    default -> throw new AssertionError(method.getName());
                });
        ResultSet rs = (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getInt" -> 1;
                    case "getBoolean" -> true;
                    case "getString" -> switch ((String) args[0]) {
                        case "title" -> "Test Book";
                        case "author" -> "Test Author";
                        default -> "Test";
                    };
                    case "getMetaData" -> metadata;
                    case "getObject" -> {
                        if (!hasYear) throw new AssertionError("Missing column must not be read");
                        yield year;
                    }
                    default -> throw new AssertionError(method.getName());
                });
        var mapper = BookDaoimpl.class.getDeclaredMethod("mapBook", ResultSet.class);
        mapper.setAccessible(true);
        return (Book) mapper.invoke(new BookDaoimpl(), rs);
    }
}
