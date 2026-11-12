<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Manage books - LIBIS</title><link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page management-page">
<%@ include file="LibrarianNav.jsp" %>
<h1>Manage books</h1>
<c:if test="${not empty errorMessage}"><p class="alert" role="alert"><c:out value="${errorMessage}"/></p></c:if>
<c:if test="${not empty errors}"><div class="alert" role="alert"><ul><c:forEach items="${errors}" var="error"><li><c:out value="${error.value}"/></li></c:forEach></ul></div></c:if>
<c:if test="${not empty successMessage}"><p class="success-message" role="status"><c:out value="${successMessage}"/></p></c:if>
<a class="btn-link" href="${pageContext.request.contextPath}/librarian/books/create">Add book</a>
<div class="search-bar"><form method="get" action="${pageContext.request.contextPath}/librarian/books">
<label class="visually-hidden" for="search">Search books</label><input type="text" id="search" name="search" placeholder="Title or author" value="<c:out value='${param.search}'/>">
<label class="visually-hidden" for="filterBy">Search field</label><select id="filterBy" name="filterBy"><option value="all">All fields</option><option value="title" ${param.filterBy == 'title' ? 'selected' : ''}>Title</option><option value="author" ${param.filterBy == 'author' ? 'selected' : ''}>Author</option></select><button type="submit">Search</button></form></div>
<c:if test="${empty books}"><p>No books found.</p></c:if>
<div class="table-card"><table><thead><tr><th>ISBN</th><th>Title</th><th>Author</th><th>Publication year</th><th>Availability</th><th>Actions</th></tr></thead><tbody>
<c:forEach items="${books}" var="book"><tr><td><c:out value="${book.isbn}"/></td><td><c:out value="${book.title}"/></td><td><c:out value="${book.author}"/></td><td><c:out value="${book.publicationYear}"/></td>
<td><c:choose><c:when test="${book.available}"><span class="badge badge-available">Available</span></c:when><c:otherwise><span class="badge badge-checked-out">On loan</span></c:otherwise></c:choose></td>
<td class="actions"><a href="${pageContext.request.contextPath}/librarian/books/edit?id=<c:out value='${book.bookId}'/>">Edit</a> <a href="${pageContext.request.contextPath}/librarian/books/delete?id=<c:out value='${book.bookId}'/>">Delete</a></td></tr></c:forEach>
</tbody></table></div></main></body></html>
