<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Manage members - LIBIS</title><link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page management-page">
<%@ include file="LibrarianNav.jsp" %>
<h1>Manage members</h1>
<c:if test="${not empty errorMessage}"><p class="alert" role="alert"><c:out value="${errorMessage}"/></p></c:if>
<c:if test="${not empty errors}"><div class="alert" role="alert"><ul><c:forEach items="${errors}" var="error"><li><c:out value="${error.value}"/></li></c:forEach></ul></div></c:if>
<c:if test="${not empty successMessage}"><p class="success-message" role="status"><c:out value="${successMessage}"/></p></c:if>
<a class="btn-link" href="${pageContext.request.contextPath}/librarian/members/create">Add member</a>
<div class="search-bar"><form method="get" action="${pageContext.request.contextPath}/librarian/members">
<label class="visually-hidden" for="search">Search members</label><input type="text" id="search" name="search" placeholder="Name or member number" value="<c:out value='${param.search}'/>">
<label class="visually-hidden" for="filterBy">Search field</label><select id="filterBy" name="filterBy"><option value="all">All fields</option><option value="name" ${param.filterBy == 'name' ? 'selected' : ''}>Name</option><option value="memberNumber" ${param.filterBy == 'memberNumber' ? 'selected' : ''}>Member number</option></select><button type="submit">Search</button></form></div>
<c:if test="${empty members}"><p>No members found.</p></c:if>
<div class="table-card"><table><thead><tr><th>Member number</th><th>Full name</th><th>Email</th><th>Username</th><th>Actions</th></tr></thead><tbody>
<c:forEach items="${members}" var="member"><tr><td><c:out value="${member.member_No}"/></td><td><c:out value="${member.fullName}"/></td><td><c:out value="${member.email}"/></td><td><c:out value="${member.username}"/></td>

<td class="actions"><a href="${pageContext.request.contextPath}/librarian/members/edit?id=<c:out value='${member.memberId}'/>">Edit</a> <a href="${pageContext.request.contextPath}/librarian/members/delete?id=<c:out value='${member.memberId}'/>">Delete</a></td></tr></c:forEach>
</tbody></table></div></main></body></html>
