package com.agritrace.backend.dao;

import com.agritrace.backend.dto.auth.AuthAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthUserMapper {
    @Select("""
            SELECT id, username, password_hash, display_name, role_code, status
            FROM sys_user WHERE username = #{username} LIMIT 1
            """)
    @Results({
        @Result(column = "id", property = "id", id = true),
        @Result(column = "username", property = "username"),
        @Result(column = "password_hash", property = "passwordHash"),
        @Result(column = "display_name", property = "displayName"),
        @Result(column = "role_code", property = "roleCode"),
        @Result(column = "status", property = "status")
    })
    AuthAccount findByUsername(@Param("username") String username);

    @Insert("""
            INSERT INTO sys_user
              (username, password_hash, display_name, role_code, status)
            VALUES (#{username}, #{passwordHash}, #{displayName}, 'ADMIN', 1)
            """)
    int insertAdmin(@Param("username") String username,
                    @Param("passwordHash") String passwordHash,
                    @Param("displayName") String displayName);
}
