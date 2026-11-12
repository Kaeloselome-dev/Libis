<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Delete member - LIBIS</title><link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page management-page">
<%@ include file="LibrarianNav.jsp" %>
<h1>Delete member</h1>
<c:if test="${not empty errorMessage}"><p class="alert" role="alert"><c:out value="${errorMessage}"/></p></c:if>
<c:if test="${not empty errors}"><div class="alert" role="alert"><ul><c:forEach items="${errors}" var="error"><li><c:out value="${error.value}"/></li></c:forEach></ul></div></c:if>
<p>Delete <strong><c:out value="${member.fullName}"/></strong> (<c:out value="${member.member_No}"/>)?</p>
<p>This also removes their borrowing history. This action cannot be undone.</p>
<form method="post" action="${pageContext.request.contextPath}/librarian/members/delete">
<input type="hidden" name="id" value="<c:out value='${member.memberId}'/>">
<button class="danger-button" type="submit">Confirm deletion</button> <a href="${pageContext.request.contextPath}/librarian/members">Cancel</a></form></main></body></html>
