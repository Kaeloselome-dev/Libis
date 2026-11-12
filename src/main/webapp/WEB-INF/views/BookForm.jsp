<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Save book - LIBIS</title><link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page management-page">
<%@ include file="LibrarianNav.jsp" %>
<h1>Save book</h1>
<c:if test="${not empty errorMessage}"><p class="alert" role="alert"><c:out value="${errorMessage}"/></p></c:if>
<c:if test="${not empty errors}"><div class="alert" role="alert"><ul><c:forEach items="${errors}" var="error"><li><c:out value="${error.value}"/></li></c:forEach></ul></div></c:if>
<form class="management-form" method="post" action="${pageContext.request.contextPath}/librarian/books/${book.bookId == 0 ? 'create' : 'edit'}">
<input type="hidden" name="id" value="<c:out value='${book.bookId}'/>">
<div class="field"><label for="isbn">ISBN</label><input id="isbn" name="isbn" type="text" maxlength="20" value="<c:out value='${book.isbn}'/>" required><c:if test="${not empty errors.isbn}"><small class="field-error"><c:out value="${errors.isbn}"/></small></c:if></div>
<div class="field"><label for="title">Title</label><input id="title" name="title" type="text" maxlength="150" value="<c:out value='${book.title}'/>" required><c:if test="${not empty errors.title}"><small class="field-error"><c:out value="${errors.title}"/></small></c:if></div>
<div class="field"><label for="author">Author</label><input id="author" name="author" type="text" maxlength="100" value="<c:out value='${book.author}'/>" required><c:if test="${not empty errors.author}"><small class="field-error"><c:out value="${errors.author}"/></small></c:if></div>
<div class="field"><label for="genre">Genre (optional)</label><input id="genre" name="genre" type="text" maxlength="50" value="<c:out value='${book.genre}'/>" ><c:if test="${not empty errors.genre}"><small class="field-error"><c:out value="${errors.genre}"/></small></c:if></div>
<div class="field"><label for="publicationYear">Publication year (optional)</label><input id="publicationYear" name="publicationYear" type="text" maxlength="10" value="<c:out value='${publicationYear}'/>" ><c:if test="${not empty errors.publicationYear}"><small class="field-error"><c:out value="${errors.publicationYear}"/></small></c:if></div>
<div class="field"><label for="synopsis">Synopsis (optional)</label><textarea id="synopsis" name="synopsis" rows="4"><c:out value="${book.synopsis}"/></textarea><c:if test="${not empty errors.synopsis}"><small class="field-error"><c:out value="${errors.synopsis}"/></small></c:if></div>
<button type="submit">Save book</button> <a href="${pageContext.request.contextPath}/librarian/books">Cancel</a></form></main></body></html>
