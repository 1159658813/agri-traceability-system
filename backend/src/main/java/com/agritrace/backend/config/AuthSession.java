package com.agritrace.backend.config;

import com.agritrace.backend.vo.auth.LoginUser;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public final class AuthSession {
    public static final String USER = "LOGIN_USER";
    public static final String CSRF_TOKEN = "CSRF_TOKEN";
    private static final SecureRandom RANDOM = new SecureRandom();

    private AuthSession() { }

    public static LoginUser getUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(USER);
        return value instanceof LoginUser user ? user : null;
    }

    public static String issueCsrfToken(HttpSession session) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_TOKEN, token);
        return token;
    }

    public static String csrfToken(HttpSession session) {
        return session == null ? null : (String) session.getAttribute(CSRF_TOKEN);
    }
}
