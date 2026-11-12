<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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

<!DOCTYPE html>
<html lang="${sessionScope.language}">
<head><meta charset="UTF-8"><title><fmt:message key="loans.pageTitle"/></title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page">
<form method="get" action="${pageContext.request.contextPath}/member/loans">
    <label for="language"><fmt:message key="language.label"/></label>
    <select id="language" name="lang">
        <option value="en" ${sessionScope.language == 'en' ? 'selected' : ''}><fmt:message key="language.english"/></option>
        <option value="tn" ${sessionScope.language == 'tn' ? 'selected' : ''}><fmt:message key="language.setswana"/></option>
    </select>
    <button type="submit"><fmt:message key="language.apply"/></button>
</form>
<h1><fmt:message key="loans.pageTitle"/></h1>
<p><c:out value="${loggedInMember.fullName}"/></p>
<p><fmt:message key="loans.count"><fmt:param value="${fn:length(openLoans)}"/><fmt:param value="${maxLoans}"/></fmt:message></p>
<c:if test="${not empty errorMessage}"><p role="alert"><c:out value="${errorMessage}"/></p></c:if>
<h2><fmt:message key="loans.open"/></h2>
<c:if test="${empty openLoans}"><p><fmt:message key="loans.emptyOpen"/></p></c:if>
<table><thead><tr><th><fmt:message key="book.title"/></th><th><fmt:message key="book.author"/></th><th><fmt:message key="loans.borrowedOn"/></th><th><fmt:message key="common.action"/></th></tr></thead><tbody>
<c:forEach items="${openLoans}" var="loan">
<tr><td><c:out value="${loan.book.title}"/></td><td><c:out value="${loan.book.author}"/></td><td><c:out value="${loan.borrowedOn}"/></td>
<td><form method="post" action="${pageContext.request.contextPath}/member/loans/return">
<input type="hidden" name="loanId" value="<c:out value='${loan.loanId}'/>"><button type="submit"><fmt:message key="common.return"/></button>
</form></td></tr>
</c:forEach>
</tbody></table>
<h2><fmt:message key="loans.history"/></h2>
<c:if test="${empty loanHistory}"><p><fmt:message key="loans.emptyHistory"/></p></c:if>
<table><thead><tr><th><fmt:message key="book.title"/></th><th><fmt:message key="book.author"/></th><th><fmt:message key="loans.borrowedOn"/></th><th><fmt:message key="loans.returnedOn"/></th></tr></thead><tbody>
<c:forEach items="${loanHistory}" var="loan">
<tr><td><c:out value="${loan.book.title}"/></td><td><c:out value="${loan.book.author}"/></td><td><c:out value="${loan.borrowedOn}"/></td>
<td><c:out value="${loan.returnedOn}"/></td></tr>
</c:forEach>
</tbody></table>
<a href="${pageContext.request.contextPath}/catalogue"><fmt:message key="loans.back"/></a>
</main></body></html>
