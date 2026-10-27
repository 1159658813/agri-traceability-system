package com.agritrace.backend.vo.auth;

import java.io.Serializable;

public record LoginUser(Long id, String username, String displayName,
                        String roleCode) implements Serializable {
    private static final long serialVersionUID = 1L;
}
