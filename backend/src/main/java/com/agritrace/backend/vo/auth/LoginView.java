package com.agritrace.backend.vo.auth;

public record LoginView(Long id, String username, String displayName,
                        String roleCode, String csrfToken) {
    public static LoginView of(LoginUser user, String csrfToken) {
        return new LoginView(user.id(), user.username(), user.displayName(),
                user.roleCode(), csrfToken);
    }
}
