<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 9/26/2026
  Time: 7:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>

<%-- Which result sections to show for the chosen filter --%>
<c:set var="filter" value="${param.filterBy}"/>
<c:set var="memberFilter" value="${filter == 'name' || filter == 'id' || filter == 'email'}"/>
<c:set var="bookFilter" value="${filter == 'title' || filter == 'author' || filter == 'genre'}"/>

<div class="search-page">

    <div class="search-bar">
        <form action="search-servlet" method="get">
            <input type="text" name="query" placeholder="Search members or books..."
                   value="${fn:escapeXml(param.query)}">

            <select name="filterBy">
                <option value="all" ${filter == 'all' ? 'selected' : ''}>All fields</option>
                <optgroup label="Members">
                    <option value="name" ${filter == 'name' ? 'selected' : ''}>Name</option>
                    <option value="id" ${filter == 'id' ? 'selected' : ''}>Member ID</option>
                    <option value="email" ${filter == 'email' ? 'selected' : ''}>Email</option>
                </optgroup>
                <optgroup label="Books">
                    <option value="title" ${filter == 'title' ? 'selected' : ''}>Title</option>
                    <option value="author" ${filter == 'author' ? 'selected' : ''}>Author</option>
                    <option value="genre" ${filter == 'genre' ? 'selected' : ''}>Genre</option>
                </optgroup>
            </select>

            <button type="submit">Search</button>
        </form>
    </div>

    <div class="results-area">
        <c:choose>
            <c:when test="${empty fn:trim(param.query)}">
                <p class="empty-state">No results yet — try a search above.</p>
            </c:when>
            <c:otherwise>
                <c:if test="${not bookFilter}">
                    <h2>Members</h2>
                    <c:choose>
                        <c:when test="${empty memberResults}">
                            <p class="empty-state">No members found matching "<c:out value='${param.query}'/>".</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="member" items="${memberResults}">
                                <div class="result-card">
                                    <h3><c:out value="${member.fullName}"/> (ID: ${member.memberId})</h3>
                                    <p>Email: <c:out value="${member.email}"/></p>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:if>

                <c:if test="${not memberFilter}">
                    <h2>Books</h2>
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
            </c:otherwise>
        </c:choose>
    </div>

</div>

</body>
</html>
