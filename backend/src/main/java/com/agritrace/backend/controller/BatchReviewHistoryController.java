package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.service.AuthService;
import com.agritrace.backend.service.BatchReviewHistoryService;
import com.agritrace.backend.service.BatchReviewProblem;
import com.agritrace.backend.vo.admin.BatchReviewHistory;
import com.agritrace.backend.vo.auth.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batches")
public class BatchReviewHistoryController {
    private final BatchReviewHistoryService historyService;
    private final AuthService authService;

    public BatchReviewHistoryController(BatchReviewHistoryService historyService, AuthService authService) {
        this.historyService = historyService;
        this.authService = authService;
    }

    @GetMapping("/{batchId}/history")
    public ResponseEntity<AuthResult<BatchReviewHistory>> history(@PathVariable Long batchId,
                                                                  HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !authService.isActive(user)) {
            throw new BatchReviewProblem(401, "请先登录");
        }
        if (!"ADMIN".equals(user.roleCode())) {
            throw new BatchReviewProblem(403, "没有管理员操作权限");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(historyService.get(batchId)));
    }

    @ExceptionHandler(BatchReviewProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(BatchReviewProblem error) {
        return ResponseEntity.status(error.getStatus()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(error.getStatus(), error.getMessage()));
    }
}
