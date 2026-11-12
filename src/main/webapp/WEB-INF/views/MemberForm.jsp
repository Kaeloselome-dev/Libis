<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Save member - LIBIS</title><link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page management-page">
<%@ include file="LibrarianNav.jsp" %>
<h1>Save member</h1>
<c:if test="${not empty errorMessage}"><p class="alert" role="alert"><c:out value="${errorMessage}"/></p></c:if>
<c:if test="${not empty errors}"><div class="alert" role="alert"><ul><c:forEach items="${errors}" var="error"><li><c:out value="${error.value}"/></li></c:forEach></ul></div></c:if>
<form class="management-form" method="post" action="${pageContext.request.contextPath}/librarian/members/${member.memberId == 0 ? 'create' : 'edit'}">
<input type="hidden" name="id" value="<c:out value='${member.memberId}'/>">
<div class="field"><label for="memberNumber">Member number</label><input id="memberNumber" name="memberNumber" type="text" maxlength="20" value="<c:out value='${member.member_No}'/>" required><c:if test="${not empty errors.memberNumber}"><small class="field-error"><c:out value="${errors.memberNumber}"/></small></c:if></div>
<div class="field"><label for="firstName">First name</label><input id="firstName" name="firstName" type="text" maxlength="50" value="<c:out value='${member.firstName}'/>" required><c:if test="${not empty errors.firstName}"><small class="field-error"><c:out value="${errors.firstName}"/></small></c:if></div>
<div class="field"><label for="lastName">Last name</label><input id="lastName" name="lastName" type="text" maxlength="50" value="<c:out value='${member.lastName}'/>" required><c:if test="${not empty errors.lastName}"><small class="field-error"><c:out value="${errors.lastName}"/></small></c:if></div>
<div class="field"><label for="email">Email</label><input id="email" name="email" type="email" maxlength="100" value="<c:out value='${member.email}'/>" required><c:if test="${not empty errors.email}"><small class="field-error"><c:out value="${errors.email}"/></small></c:if></div>
<div class="field"><label for="username">Username</label><input id="username" name="username" type="text" maxlength="50" value="<c:out value='${member.username}'/>" required><c:if test="${not empty errors.username}"><small class="field-error"><c:out value="${errors.username}"/></small></c:if></div>
<div class="field"><label for="phoneNumber">Phone number (optional)</label><input id="phoneNumber" name="phoneNumber" type="text" maxlength="20" value="<c:out value='${member.phoneNumber}'/>" ><c:if test="${not empty errors.phoneNumber}"><small class="field-error"><c:out value="${errors.phoneNumber}"/></small></c:if></div>
<div class="field"><label for="password">Password (leave blank when editing to keep the current password)</label><input id="password" type="password" name="password" maxlength="100" autocomplete="new-password" ${member.memberId == 0 ? 'required' : ''}><c:if test="${not empty errors.password}"><small class="field-error"><c:out value="${errors.password}"/></small></c:if></div>
<button type="submit">Save member</button> <a href="${pageContext.request.contextPath}/librarian/members">Cancel</a></form></main></body></html>
