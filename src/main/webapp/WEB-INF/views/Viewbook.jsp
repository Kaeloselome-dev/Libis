<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 10/6/2026
  Time: 9:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="book" type="bac.ac.libis.model.Book"--%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LIBIS - View Book</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>

<div class="catalogue-page">
    <c:choose>
        <c:when test="${empty book}">
            <p class="empty-state">Book not found.</p>
        </c:when>
        <c:otherwise>
            <div class="result-card">
                <h2><c:out value="${book.title}"/></h2>
                <p>Book ID: <c:out value="${book.bookId}"/></p>
                <p>Author: <c:out value="${book.author}"/></p>
                <p>Genre: <c:out value="${book.genre}"/></p>
                <p>Synopsis: <c:out value="${book.synopsis}"/></p>
                <p>
                    <c:choose>
                        <c:when test="${book.available}">
                            <span class="badge badge-available">Available</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-checked-out">Checked Out</span>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </c:otherwise>
    </c:choose>

    <a class="btn-link" href="${pageContext.request.contextPath}/catalogue">Back to catalogue</a>
</div>

</body>
</html>
