

package bac.ac.libis.data;

import bac.ac.libis.model.Book;
import bac.ac.libis.model.Member;

import java.util.ArrayList;
import java.util.List;

public class Seedings {


    public static final List<Book> books = new ArrayList<>();
    public static final List<Member> members = new ArrayList<>();

    static {


        books.add(new Book(1001, "48 Laws of Power", "Ene oo", "Educational", "What", true));
        books.add(new Book(1002, "1984", "George Orwell", "Educational", "What", true));
        books.add(new Book(1003, "The Catcher in the Rye", "J.D. Salinger", "Fiction", "What", true));
        books.add(new Book(1004, "The Great Gatsby", "Scott Fitzgerald", "Classic", "What", true));
        books.add(new Book(1005, "The Hobbit", "J.R.R. Tolkien", "Fiction", "What", true));
        books.add(new Book(1006, "Fahrenheit 451", "Ray Bradbury", "Educational", "What", true));
        books.add(new Book(1007, "The Alchemist", "Ene oo", "Educational", "What", true));
        books.add(new Book(1008, "Brave New World", "Aldous Huxley", "Fiction", "What", true));
        books.add(new Book(1009, "Clean Code", "Robert C. Martin", "Software Engineering", "What", true));
        books.add(new Book(1010, "To Kill a Mockingbird", "Harper Lee", "Educational", "What", true));



        members.add(new Member(1001, "Kaelo", "Kaelo", "Selome", "Kaelo@gmail.com","+26773442232", "Kaelo12345", "member"));
        members.add(new Member(1002,"Tom", "Tom", "Doe", "Tom@gmail.com","+26774658897", "password", "member"));
        members.add(new Member(1003,"John", "John", "Doe", "John@gmail.com", "+26779885644","password", "member"));
        members.add(new Member(1004,"Bob", "Bob", "Doe", "Bob@gmail.com", "+26778332243","password", "member"));
        members.add(new Member(1005,"R_Doe", "Rasta", "Doe", "Rasta@gmail.com", "+26771232345","password", "member"));

}
}

