package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.AuthUserMapper;
import com.agritrace.backend.dto.auth.AuthAccount;
import com.agritrace.backend.service.AuthService;
import com.agritrace.backend.vo.auth.LoginUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final String unusedHash;

    public AuthServiceImpl(AuthUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.unusedHash = passwordEncoder.encode("not-a-real-user-password");
    }

    @Override
    public LoginUser verify(String username, String password) {
        if (username == null || username.isBlank() || username.length() > 50
                || password == null || password.isBlank() || password.length() > 128) {
            return null;
        }

        AuthAccount account = userMapper.findByUsername(username.trim());
        String hash = account == null ? unusedHash : account.getPasswordHash();
        boolean validPassword = hash != null && passwordEncoder.matches(password, hash);
        if (account == null || !validPassword || !Integer.valueOf(1).equals(account.getStatus())) {
            return null;
        }

        if (!"ADMIN".equals(account.getRoleCode())) {
            return null;
        }
        return new LoginUser(account.getId(), account.getUsername(),
                account.getDisplayName(), account.getRoleCode());
    }

    @Override
    public boolean isActive(LoginUser user) {
        if (user == null) {
            return false;
        }
        AuthAccount account = userMapper.findByUsername(user.username());
        return account != null && user.id().equals(account.getId())
                && Integer.valueOf(1).equals(account.getStatus())
                && user.roleCode().equals(account.getRoleCode());
    }
}
