<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 9/7/2026
  Time: 10:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="books" type="java.util.List<bac.ac.libis.model.Book>"--%>
<%--@elvariable id="loggedInMember" type="bac.ac.libis.model.Member"--%>

<!DOCTYPE html>
<html>
<head>
    <title>LIBIS - Book Catalogue</title>
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #f4f4f4; }
        .actions a { margin-right: 8px; text-decoration: none; color: #0066cc; }
    </style>
</head>
<body>

<!-- Personalized greeting using session/request scope attribute -->
<h2>Welcome, ${loggedInMember.firstName} ${loggedInMember.lastName}!</h2>
<p>Here is the current LIBIS book catalogue:</p>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Author</th>
        <th>Genre</th>
        <th>Availability</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${books}" var="book">
        <tr>
            <td>${book.bookId}</td>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.genre}</td>
            <td>
                <c:choose>
                    <c:when test="${book.available}">Available</c:when>
                    <c:otherwise>Checked Out</c:otherwise>
                </c:choose>
            </td>
            <td>
                <a href="#">Search</a> |
                <a href="#">Borrow</a> |
                <a href="#">Reserve</a>
            </td>
        </tr>
    </c:forEach>

    </tbody>
</table>

</body>
</html>
