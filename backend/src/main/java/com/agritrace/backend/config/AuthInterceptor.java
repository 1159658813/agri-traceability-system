package com.agritrace.backend.config;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.controller.AuthController;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public AuthInterceptor(ObjectMapper objectMapper, AuthService authService) {
        this.objectMapper = objectMapper;
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) &&
                (path.startsWith("/api/public/") || path.equals("/api/system/status"))) {
            return true;
        }

        HttpSession session = request.getSession(false);
        LoginUser user = AuthSession.getUser(session);
        if (user == null || !authService.isActive(user)) {
            if (session != null) {
                session.invalidate();
            }
            writeError(response, 401, "请先登录");
            return false;
        }
        // 农户接口尚未完成数据归属校验，现阶段先拒绝农户访问业务接口。
        if (!"ADMIN".equals(user.roleCode())) {
            writeError(response, 403, "当前账号没有操作权限");
            return false;
        }
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
            if (!AuthController.sameToken(AuthSession.csrfToken(session),
                                           request.getHeader("X-CSRF-Token"))) {
                writeError(response, 403, "请求校验失败");
                return false;
            }
        }
        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Cache-Control", "no-store");
        objectMapper.writeValue(response.getWriter(), AuthResult.error(status, message));
    }
}
