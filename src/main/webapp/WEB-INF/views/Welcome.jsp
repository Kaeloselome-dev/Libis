<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 9/7/2026
  Time: 10:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

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

    <c:set var="filter" value="${param.filterBy}"/>
    <div class="search-bar">
        <form action="${pageContext.request.contextPath}/catalogue" method="get">
            <input type="text" name="search" placeholder="Search books..."
                   value="<c:out value='${param.search}'/>">

            <select name="filterBy">
                <option value="all" ${filter == 'all' ? 'selected' : ''}>All fields</option>
                <option value="title" ${filter == 'title' ? 'selected' : ''}>Title</option>
                <option value="author" ${filter == 'author' ? 'selected' : ''}>Author</option>
                <option value="genre" ${filter == 'genre' ? 'selected' : ''}>Genre</option>
            </select>

            <button type="submit">Search</button>
        </form>
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
                        <c:url var="searchUrl" value="/catalogue">
                            <c:param name="search" value="${book.title}"/>
                            <c:param name="filterBy" value="title"/>
                        </c:url>
                        <a href="${searchUrl}">Search</a>
                        <a href="#">Borrow</a>
                        <a href="#">Reserve</a>
                    </td>
                </tr>
            </c:forEach>            </tbody>
        </table>
    </div>

    <a class="btn-link" href="${pageContext.request.contextPath}/logout">Log out</a>

</div>

</body>
</html>