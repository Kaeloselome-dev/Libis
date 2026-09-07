<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign in</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-4">

<div class="card shadow-sm p-4" style="max-width: 420px; width: 100%;">
    <div class="card-body p-0">
        <h1 class="h3 fw-bold text-center mb-1">Sign in</h1>
        <p class="text-muted text-center mb-4">Access your card, holds, and loans</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger py-2" role="alert">
                    ${errorMessage}
            </div>
        </c:if>

        <form action="login-servlet" method="Post">
            <div class="mb-3">
                <label for="Email" class="form-label">Email</label>
                <input type="text" class="form-control" id="email" name="email" autocomplete="email" required>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" name="password" autocomplete="current-password" required>
            </div>

            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="keepSignedIn" name="keepSignedIn">
                <label class="form-check-label" for="keepSignedIn">Keep me signed in on this device</label>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 mb-3">Sign in</button>
        </form>
    </div>
</div>

<!-- Bootstrap Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>