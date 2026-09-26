package bac.ac.libis.data;

import bac.ac.libis.model.Book;
import bac.ac.libis.model.Member;
import java.util.ArrayList;
import java.util.List;


public class LibraryData {
    public static final List<Book> books = new ArrayList<>();
    public static final List<Member> members = new ArrayList<>();
    

    static {
        books.add(new Book(1001, "48 Laws of Power", "Ene oo", "Educational", "What", true));
        books.add(new Book(1002, "1984", "George Orwell", "Educational", "What", true));
        books.add(new Book(1001, "The Catcher in the Rye", "J.D. Salinger", "Fiction", "What", true));
        books.add(new Book(1001, "The Great Gatsby", "Scott Fitzgerald", "Classic", "What", true));
        books.add(new Book(1001, "The Hobbit", "J.R.R. Tolkien", "Fiction", "What", true));
        books.add(new Book(1001, "Fahrenheit 451", "Ray Bradbury", "Educational", "What", true));
        books.add(new Book(1001, "The Alchemist", "Ene oo", "Educational", "What", true));
        books.add(new Book(1001, "Brave New World", "Aldous Huxley", "Fiction", "What", true));
        books.add(new Book(1001, "Clean Code", "Robert C. Martin", "Software Engineering", "What", true));
        books.add(new Book(1001, "To Kill a Mockingbird", "Harper Lee", "Educational", "What", true));



        members.add(new Member(1001, "Kaelo", "Selome", "Kaelo@gmail.com","", "Kaelo12345"));
        members.add(new Member(1002, "Tom", "Doe", "Tom@gmail.com","", "password"));
        members.add(new Member(1003, "John", "Doe", "John@gmail.com", "","password"));
        members.add(new Member(1004, "Bob", "Doe", "Bob@gmail.com", "","password"));
        members.add(new Member(1005, "Rasta", "Doe", "Rasta@gmail.com", "","password"));


    }
}