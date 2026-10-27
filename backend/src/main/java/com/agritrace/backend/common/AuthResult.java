package com.agritrace.backend.common;

public record AuthResult<T>(int code, String message, T data) {
    public static <T> AuthResult<T> ok(T data) {
        return new AuthResult<>(200, "操作成功", data);
    }

    public static <T> AuthResult<T> error(int code, String message) {
        return new AuthResult<>(code, message, null);
    }
}
