<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><title>My Loans</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"></head>
<body><main class="catalogue-page">
<h1>My Loans</h1>
<p><c:out value="${loggedInMember.fullName}"/></p>
<h2>Open loans</h2>
<c:if test="${empty openLoans}"><p>You have no open loans.</p></c:if>
<table><thead><tr><th>Book ID</th><th>Borrowed on</th><th>Action</th></tr></thead><tbody>
<c:forEach items="${openLoans}" var="loan">
<tr><td><c:out value="${loan.bookId}"/></td><td><c:out value="${loan.borrowedOn}"/></td>
<td><form method="post" action="${pageContext.request.contextPath}/member/loans/return">
<input type="hidden" name="loanId" value="${loan.loanId}"><button type="submit">Return</button>
</form></td></tr>
</c:forEach>
</tbody></table>
<h2>Returned loans</h2>
<c:if test="${empty loanHistory}"><p>You have no returned loans.</p></c:if>
<table><thead><tr><th>Book ID</th><th>Borrowed on</th><th>Returned on</th></tr></thead><tbody>
<c:forEach items="${loanHistory}" var="loan">
<tr><td><c:out value="${loan.bookId}"/></td><td><c:out value="${loan.borrowedOn}"/></td>
<td><c:out value="${loan.returnedOn}"/></td></tr>
</c:forEach>
</tbody></table>
<a href="${pageContext.request.contextPath}/catalogue">Back to catalogue</a>
</main></body></html>
