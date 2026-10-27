package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.farmer.ReviewRequest;
import com.agritrace.backend.service.AuthService;
import com.agritrace.backend.service.FarmerReviewService;
import com.agritrace.backend.service.ReviewProblem;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.ApplicationPage;
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
@RequestMapping("/api/admin/farmer-applications")
public class FarmerReviewController {
    private final FarmerReviewService reviewService;
    private final AuthService authService;

    public FarmerReviewController(FarmerReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<AuthResult<ApplicationPage>> list(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        checkAdmin(request);
        return ok(reviewService.findPage(status, pageNum, pageSize));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<AuthResult<Void>> review(@PathVariable Long id,
                                                    @RequestBody ReviewRequest body,
                                                    HttpServletRequest request) {
        LoginUser admin = checkAdmin(request);
        if (body == null) {
            throw new ReviewProblem(400, "请选择审核结果");
        }
        reviewService.review(id, admin.id(), body.decision(), body.comment());
        return ok(null);
    }

    private LoginUser checkAdmin(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !authService.isActive(user)) {
            throw new ReviewProblem(401, "请先登录");
        }
        if (!"ADMIN".equals(user.roleCode())) {
            throw new ReviewProblem(403, "没有管理员操作权限");
        }
        return user;
    }

    @ExceptionHandler(ReviewProblem.class)
    public ResponseEntity<AuthResult<Void>> handleReviewProblem(ReviewProblem error) {
        return ResponseEntity.status(error.getStatus())
                .cacheControl(CacheControl.noStore())
                .body(AuthResult.error(error.getStatus(), error.getMessage()));
    }

    private static <T> ResponseEntity<AuthResult<T>> ok(T data) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(data));
    }
}
