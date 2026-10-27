package com.agritrace.backend.service;

import com.agritrace.backend.vo.auth.LoginUser;

public interface AuthService {
    LoginUser verify(String username, String password);

    boolean isActive(LoginUser user);
}
