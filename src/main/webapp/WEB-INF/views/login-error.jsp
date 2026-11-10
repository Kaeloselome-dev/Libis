<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign-in failed | LIBIS</title>
    <style>
        :root {
            --bg: #e8ebf4;
            --card: #f1f3f9;
            --ink: #232738;
            --muted: #6b7088;
            --accent: #6c5ce7;
            --accent-dark: #5646d6;
            --error-bg: #fbe3e6;
            --error-ink: #b4233a;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            background: var(--bg);
            color: var(--ink);
            font-family: "Segoe UI", system-ui, -apple-system, sans-serif;
        }

        .panel {
            width: 100%;
            max-width: 420px;
            padding: 32px;
            background: var(--card);
            border-radius: 20px;
            box-shadow: 10px 10px 24px rgba(163, 170, 196, 0.45),
                        -8px -8px 20px rgba(255, 255, 255, 0.85);
        }

        h1 {
            margin: 0 0 6px;
            font-size: 1.5rem;
        }

        .subtitle {
            margin: 0 0 20px;
            color: var(--muted);
        }

        .error {
            margin: 0 0 24px;
            padding: 12px 14px;
            border-radius: 12px;
            background: var(--error-bg);
            color: var(--error-ink);
            font-weight: 600;
            line-height: 1.45;
        }

        .button {
            display: inline-block;
            padding: 11px 22px;
            border-radius: 12px;
            background: linear-gradient(135deg, var(--accent), var(--accent-dark));
            color: #fff;
            font-weight: 700;
            text-decoration: none;
            box-shadow: 4px 4px 12px rgba(86, 70, 214, 0.35);
        }

        .button:focus-visible {
            outline: 3px solid var(--ink);
            outline-offset: 3px;
        }
    </style>
</head>
<body>
    <main class="panel">
        <h1>Sign-in failed</h1>
        <p class="subtitle">LIBIS library system</p>

        <p class="error" role="alert">
            The username or password did not match. Check both and try again.
        </p>

        <a class="button" href="<c:url value='/catalogue'/>">Back to sign in</a>
    </main>
</body>
</html>
