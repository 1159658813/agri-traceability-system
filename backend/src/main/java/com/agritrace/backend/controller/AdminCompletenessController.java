package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.service.AuthService;
import com.agritrace.backend.service.BatchReviewProblem;
import com.agritrace.backend.service.CompletenessReviewService;
import com.agritrace.backend.vo.admin.CompletenessReviewReport;
import com.agritrace.backend.vo.auth.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batches")
public class AdminCompletenessController {
    private final CompletenessReviewService completeness;
    private final AuthService authService;

    public AdminCompletenessController(CompletenessReviewService completeness, AuthService authService) {
        this.completeness = completeness;
        this.authService = authService;
    }

    @GetMapping(params = "completenessBatchId")
    public ResponseEntity<AuthResult<CompletenessReviewReport>> report(@RequestParam("completenessBatchId") Long batchId,
                                                                        HttpServletRequest request) {
        LoginUser admin = AuthSession.getUser(request.getSession(false));
        if (admin == null || !authService.isActive(admin)) {
            throw new BatchReviewProblem(401, "请先登录");
        }
        if (!"ADMIN".equals(admin.roleCode())) {
            throw new BatchReviewProblem(403, "没有管理员操作权限");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(completeness.forAdmin(batchId)));
    }

    @ExceptionHandler(BatchReviewProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(BatchReviewProblem problem) {
        return ResponseEntity.status(problem.getStatus()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(problem.getStatus(), problem.getMessage()));
    }
}
