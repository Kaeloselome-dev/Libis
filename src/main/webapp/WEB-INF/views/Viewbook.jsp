<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 10/6/2026
  Time: 9:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>View_Book</title>
</head>
<body>
<div class="results-area">
            <c:if test="${not memberFilter}">
                <h2>Book</h2>
                <c:choose>
                    <c:when test="${empty bookResults}">
                        <p class="empty-state">No books found matching "<c:out value='${param.query}'/>".</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="book" items="${bookResults}">
                            <div class="result-card">
                                <h3><c:out value="${book.title}"/></h3>
                                <p>Author: <c:out value="${book.author}"/></p>
                                <p>Genre: <c:out value="${book.genre}"/></p>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </c:if>
</div>
</body>
</html>
