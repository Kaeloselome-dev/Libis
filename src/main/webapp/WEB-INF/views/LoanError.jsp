<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>Loan request</title></head>
<body><main><h1>Unable to complete your request</h1>
<p role="alert"><c:out value="${errorMessage}"/></p>
<a href="${pageContext.request.contextPath}/member/loans">My Loans</a>
<a href="${pageContext.request.contextPath}/catalogue">Catalogue</a>
</main></body></html>
