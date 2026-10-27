package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.service.FarmerWorkspaceService;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.FarmerOverview;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer")
public class FarmerWorkspaceController {
    private final FarmerWorkspaceService workspaceService;

    public FarmerWorkspaceController(FarmerWorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/overview")
    public ResponseEntity<AuthResult<FarmerOverview>> overview(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !"FARMER".equals(user.roleCode())) {
            return ResponseEntity.status(403).cacheControl(CacheControl.noStore())
                    .body(AuthResult.error(403, "当前账号没有操作权限"));
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(workspaceService.overview(user.id())));
    }
}
