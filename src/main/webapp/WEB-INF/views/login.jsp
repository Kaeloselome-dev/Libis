
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign in</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
</head>
<body>

<div class="card">
    <h1>Sign in</h1>
    <p class="subtitle">Access your card, holds, and loans</p>

    <c:if test="${not empty errorMessage}">
        <div class="alert" role="alert">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login"
          method="POST">

        <div class="field">
            <label for="username">Username</label>
            <input
                    type="text"
                    id="username"
                    name="j_username"
                    autocomplete="username"
                    required
            >
        </div>

        <div class="field">
            <label for="password">Password</label>
            <input
                    type="password"
                    id="password"
                    name="j_password"
                    autocomplete="current-password"
                    required
            >
        </div>

        <button type="submit">Sign in</button>

        <p class="message">
            Not registered?
            <a href="${pageContext.request.contextPath}/signup">
                Create an account
            </a>
        </p>
    </form>
</div>

</body>
</html>
