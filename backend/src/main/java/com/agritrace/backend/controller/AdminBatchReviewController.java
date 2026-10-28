package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.admin.BatchDecision;
import com.agritrace.backend.service.AdminBatchReviewService;
import com.agritrace.backend.service.AuthService;
import com.agritrace.backend.service.BatchReviewProblem;
import com.agritrace.backend.vo.admin.BatchReviewPage;
import com.agritrace.backend.vo.auth.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batches")
public class AdminBatchReviewController {
    private final AdminBatchReviewService reviewService;
    private final AuthService authService;

    public AdminBatchReviewController(AdminBatchReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<AuthResult<BatchReviewPage>> list(
            @RequestParam(defaultValue = "SUBMITTED") String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        checkAdmin(request);
        return ok(reviewService.list(status, pageNum, pageSize));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<AuthResult<Void>> review(@PathVariable Long id,
                                                   @RequestBody(required = false) BatchDecision body,
                                                   HttpServletRequest request) {
        LoginUser admin = checkAdmin(request);
        if (body == null) throw new BatchReviewProblem(400, "请选择审核结果");
        reviewService.review(id, admin.id(), body.decision(), body.comment());
        return ok(null);
    }

    private LoginUser checkAdmin(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !authService.isActive(user)) {
            throw new BatchReviewProblem(401, "请先登录");
        }
        if (!"ADMIN".equals(user.roleCode())) {
            throw new BatchReviewProblem(403, "没有管理员操作权限");
        }
        return user;
    }

    @ExceptionHandler(BatchReviewProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(BatchReviewProblem error) {
        return ResponseEntity.status(error.getStatus()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(error.getStatus(), error.getMessage()));
    }

    private static <T> ResponseEntity<AuthResult<T>> ok(T data) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(data));
    }
}
