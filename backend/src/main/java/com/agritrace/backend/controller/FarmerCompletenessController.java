package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.service.BatchCompletenessService;
import com.agritrace.backend.service.FarmerRecordProblem;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.BatchCompletenessReport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer/batches/{batchId}/completeness")
public class FarmerCompletenessController {
    private final BatchCompletenessService service;

    public FarmerCompletenessController(BatchCompletenessService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<AuthResult<BatchCompletenessReport>> report(HttpServletRequest request,
                                                                       @PathVariable Long batchId) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !"FARMER".equals(user.roleCode())) {
            throw new FarmerRecordProblem(403, "当前账号无权查看农户评分");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(service.forFarmer(user.id(), batchId)));
    }

    @ExceptionHandler(FarmerRecordProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(FarmerRecordProblem problem) {
        return ResponseEntity.status(problem.getCode()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(problem.getCode(), problem.getMessage()));
    }
}
