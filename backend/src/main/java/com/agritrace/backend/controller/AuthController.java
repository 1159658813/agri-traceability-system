package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.auth.LoginRequest;
import com.agritrace.backend.service.AuthService;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.auth.LoginView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResult<LoginView>> login(@RequestBody(required = false) LoginRequest request,
                                                        HttpServletRequest servletRequest) {
        if (request == null) {
            return fail(400, "请填写账号和密码");
        }
        LoginUser user = authService.verify(request.username(), request.password());
        if (user == null) {
            return fail(401, "账号或密码错误，或账号未启用");
        }

        HttpSession previous = servletRequest.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute(AuthSession.USER, user);
        String csrfToken = AuthSession.issueCsrfToken(session);
        return noCache(AuthResult.ok(LoginView.of(user, csrfToken)));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResult<LoginView>> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        LoginUser user = AuthSession.getUser(session);
        if (user == null || !authService.isActive(user)) {
            return fail(401, "请先登录");
        }
        return noCache(AuthResult.ok(LoginView.of(user, AuthSession.csrfToken(session))));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResult<Void>> logout(HttpServletRequest request,
                                                    @RequestHeader(value = "X-CSRF-Token", required = false)
                                                    String csrfToken) {
        HttpSession session = request.getSession(false);
        if (AuthSession.getUser(session) == null) {
            return fail(401, "请先登录");
        }
        if (!sameToken(AuthSession.csrfToken(session), csrfToken)) {
            return fail(403, "请求校验失败");
        }
        session.invalidate();
        return noCache(AuthResult.<Void>ok(null));
    }

    public static boolean sameToken(String expected, String actual) {
        return expected != null && actual != null
                && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                                          actual.getBytes(StandardCharsets.UTF_8));
    }

    private static <T> ResponseEntity<AuthResult<T>> noCache(AuthResult<T> result) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(result);
    }

    private static <T> ResponseEntity<AuthResult<T>> fail(int status, String message) {
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(status, message));
    }
}
