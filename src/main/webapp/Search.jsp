<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 9/26/2026
  Time: 7:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Members</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>

<div class="search-page">

    <div class="search-bar">
        <form action="search-servlet" method="Get">
            <input type="text" name="query" placeholder="Search by name, ID, or email..." value="${param.query}">

            <select name="filterBy">
                <option value="all" ${param.filterBy == 'all' ? 'selected' : ''}>All fields</option>
                <option value="name" ${param.filterBy == 'name' ? 'selected' : ''}>Name</option>
                <option value="id" ${param.filterBy == 'id' ? 'selected' : ''}>Member ID</option>
                <option value="email" ${param.filterBy == 'email' ? 'selected' : ''}>Email</option>
            </select>

            <button type="submit">Search</button>
        </form>
    </div>

    <<div class="results-area">
    <c:choose>
        <c:when test="${empty searchResults && not empty param.query}">
            <p class="empty-state">No members found matching "${param.query}".</p>
        </c:when>
        <c:when test="${empty searchResults}">
            <p class="empty-state">No results yet — try a search above.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="member" items="${searchResults}">
                <div class="result-card">
                    <h3>${member.fullName} (ID: ${member.memberId})</h3>
                    <p>Email: ${member.email}</p>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

</div>

</body>
</html>