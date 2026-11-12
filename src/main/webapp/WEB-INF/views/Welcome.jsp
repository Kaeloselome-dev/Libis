<%--
  Created by IntelliJ IDEA.
  User: Sir_Selome
  Date: 9/7/2026
  Time: 10:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:choose>
    <c:when test="${param.lang == 'en' or param.lang == 'tn'}">
        <c:set var="language" value="${param.lang}" scope="session"/>
    </c:when>
    <c:when test="${sessionScope.language != 'en' and sessionScope.language != 'tn'}">
        <c:set var="language" value="en" scope="session"/>
    </c:when>
</c:choose>
<fmt:setLocale value="${sessionScope.language}" scope="session"/>
<fmt:setBundle basename="messages"/>


<%--@elvariable id="books" type="java.util.List<bac.ac.libis.model.Book>"--%>
<%--@elvariable id="loggedInMember" type="bac.ac.libis.model.Member"--%>

<!DOCTYPE html>
<html lang="${sessionScope.language}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="catalogue.pageTitle"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>

<div class="catalogue-page">
<c:if test="${pageContext.request.isUserInRole('librarian')}">
    <%@ include file="LibrarianNav.jsp" %>
</c:if>
<form method="get" action="${pageContext.request.contextPath}/catalogue">
    <label for="language"><fmt:message key="language.label"/></label>
    <select id="language" name="lang">
        <option value="en" ${sessionScope.language == 'en' ? 'selected' : ''}><fmt:message key="language.english"/></option>
        <option value="tn" ${sessionScope.language == 'tn' ? 'selected' : ''}><fmt:message key="language.setswana"/></option>
    </select>
    <input type="hidden" name="search" value="<c:out value='${param.search}'/>">
    <input type="hidden" name="filterBy" value="<c:out value='${param.filterBy}'/>">
    <button type="submit"><fmt:message key="language.apply"/></button>
</form>

    <div class="catalogue-header">
        <h2><fmt:message key="catalogue.welcome"><fmt:param><c:out value="${loggedInMember.firstName}"/></fmt:param><fmt:param><c:out value="${loggedInMember.lastName}"/></fmt:param></fmt:message></h2>
        <p><fmt:message key="catalogue.description"/></p>
    </div>

    <c:set var="filter" value="${param.filterBy}"/>
    <div class="search-bar">
        <form action="${pageContext.request.contextPath}/catalogue" method="get">
            <fmt:message key="catalogue.searchPlaceholder" var="searchPlaceholder"/>
            <input type="text" name="search" placeholder="<c:out value='${searchPlaceholder}'/>"
                   value="<c:out value='${param.search}'/>">

            <select name="filterBy">
                <option value="all" ${filter == 'all' ? 'selected' : ''}><fmt:message key="catalogue.allFields"/></option>
                <option value="title" ${filter == 'title' ? 'selected' : ''}><fmt:message key="book.title"/></option>
                <option value="author" ${filter == 'author' ? 'selected' : ''}><fmt:message key="book.author"/></option>
                <option value="genre" ${filter == 'genre' ? 'selected' : ''}><fmt:message key="book.genre"/></option>
            </select>

            <button type="submit"><fmt:message key="common.search"/></button>
        </form>
    </div>

    <c:if test="${not empty error}"><p role="alert"><fmt:message key="catalogue.loadError"/></p></c:if>
    <c:if test="${empty books and empty error}"><p><fmt:message key="catalogue.empty"/></p></c:if>
    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th><fmt:message key="book.id"/></th>
                <th><fmt:message key="book.title"/></th>
                <th><fmt:message key="book.author"/></th>
                <th><fmt:message key="book.genre"/></th>
                <th><fmt:message key="book.availability"/></th>
                <th><fmt:message key="common.actions"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${books}" var="book">
                <tr>
                    <td><c:out value="${book.bookId}"/></td>
                    <td><c:out value="${book.title}"/></td>
                    <td><c:out value="${book.author}"/></td>
                    <td><c:out value="${book.genre}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${book.available}">
                                <span class="badge badge-available"><fmt:message key="book.available"/></span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-checked-out"><fmt:message key="book.checkedOut"/></span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="actions">
                        <c:url var="searchUrl" value="/catalogue">
                            <c:param name="search" value="${book.title}"/>
                            <c:param name="filterBy" value="title"/>
                        </c:url>
                        <a href="<c:out value='${searchUrl}'/>"><fmt:message key="common.search"/></a>
                        <form method="post" action="${pageContext.request.contextPath}/member/loans/borrow">
                            <input type="hidden" name="bookId" value="<c:out value='${book.bookId}'/>">
                            <button type="submit"><fmt:message key="common.borrow"/></button>
                        </form>
                        <a href="#"><fmt:message key="common.reserve"/></a>
                    </td>
                </tr>
            </c:forEach>            </tbody>
        </table>
    </div>

    <a class="btn-link" href="${pageContext.request.contextPath}/member/loans"><fmt:message key="loans.pageTitle"/></a>
    <a class="btn-link" href="${pageContext.request.contextPath}/logout"><fmt:message key="common.logout"/></a>

</div>

</body>
</html>
