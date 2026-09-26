<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                ${errorMessage}
        </div>
    </c:if>

    <form action="signup-servlet" method="Post">
        <div class="field">
            <label for="firstname">Firstname</label>
            <input type="text" id="firstname" name="firstname" autocomplete="firstname" required>
        </div>

        <div class="field">
            <label for="lastname">Lastname</label>
            <input type="text" id="lastname" name="lastname" autocomplete="lastname" required>
        </div>

        <div class="field">
            <label for="phoneNumber">Phone</label>
            <input type="text" id="phoneNumber" name="phoneNumber" autocomplete="phoneNumber" required>
        </div>

        <div class="field">
            <label for="email">Email</label>
            <input type="text" id="email" name="email" autocomplete="email" required>
        </div>

        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" autocomplete="current-password" required>
        </div>

        <button type="submit">Sign in</button>
    </form>
</div>

</body>
</html>