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
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LIBIS - Book Catalogue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>

<div class="catalogue-page">

    <div class="catalogue-header">
        <h2>Welcome, ${loggedInMember.firstName} ${loggedInMember.lastName}!</h2>
        <p>Here is the current LIBIS book catalogue:</p>
    </div>

    <div class="table-card">
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
                            <c:when test="${book.available}">
                                <span class="badge badge-available">Available</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-checked-out">Checked Out</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="actions">
                        <a href="search-servlet">Search</a>
                        <a href="#">Borrow</a>
                        <a href="#">Reserve</a>
                    </td>
                </tr>
            </c:forEach>            </tbody>
        </table>
    </div>

    <a class="btn-link" href="search-servlet">Search</a>

</div>

</body>
</html>