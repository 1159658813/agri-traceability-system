package com.agritrace.backend.config;

import com.agritrace.backend.dao.AuthUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private final AuthUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public AdminBootstrap(AuthUserMapper userMapper, PasswordEncoder passwordEncoder,
                          Environment environment) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String username = environment.getProperty("APP_BOOTSTRAP_ADMIN_USERNAME");
        String password = environment.getProperty("APP_BOOTSTRAP_ADMIN_PASSWORD");
        if (username == null && password == null) {
            return;
        }
        if (username == null || !username.matches("[a-zA-Z][a-zA-Z0-9_]{3,49}")
                || password == null || password.length() < 6 || password.length() > 128) {
            throw new IllegalStateException("管理员初始化参数不符合要求：账号4-50位，密码6-128位");
        }
        if (userMapper.findByUsername(username) != null) {
            log.info("管理员初始化已跳过：该用户名已存在");
            return;
        }
        userMapper.insertAdmin(username, passwordEncoder.encode(password), "本地管理员");
        log.info("本地管理员账号创建成功。请清除环境变量后重新启动，并妥善保管密码");
    }
}
