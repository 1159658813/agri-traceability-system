package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.farmer.CirculationForm;
import com.agritrace.backend.dto.farmer.QualityInspectionForm;
import com.agritrace.backend.service.FarmerRecordProblem;
import com.agritrace.backend.service.FarmerSupplementalRecordService;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.SupplementalRecords;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer/batches/{batchId}/records")
public class FarmerSupplementalRecordController {
    private final FarmerSupplementalRecordService service;

    public FarmerSupplementalRecordController(FarmerSupplementalRecordService service) {
        this.service = service;
    }

    @GetMapping("/extended")
    public ResponseEntity<AuthResult<SupplementalRecords>> list(HttpServletRequest request,
            @PathVariable Long batchId) {
        return ok(service.list(farmerId(request), batchId));
    }

    @PostMapping("/quality")
    public ResponseEntity<AuthResult<Void>> createQuality(HttpServletRequest request,
            @PathVariable Long batchId, @RequestBody(required = false) QualityInspectionForm form) {
        service.createQuality(farmerId(request), batchId, form);
        return ok(null);
    }

    @PutMapping("/quality/{recordId}")
    public ResponseEntity<AuthResult<Void>> updateQuality(HttpServletRequest request,
            @PathVariable Long batchId, @PathVariable Long recordId,
            @RequestBody(required = false) QualityInspectionForm form) {
        service.updateQuality(farmerId(request), batchId, recordId, form);
        return ok(null);
    }

    @PostMapping("/circulation")
    public ResponseEntity<AuthResult<Void>> createCirculation(HttpServletRequest request,
            @PathVariable Long batchId, @RequestBody(required = false) CirculationForm form) {
        service.createCirculation(farmerId(request), batchId, form);
        return ok(null);
    }

    @PutMapping("/circulation/{recordId}")
    public ResponseEntity<AuthResult<Void>> updateCirculation(HttpServletRequest request,
            @PathVariable Long batchId, @PathVariable Long recordId,
            @RequestBody(required = false) CirculationForm form) {
        service.updateCirculation(farmerId(request), batchId, recordId, form);
        return ok(null);
    }

    @ExceptionHandler(FarmerRecordProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(FarmerRecordProblem problem) {
        return ResponseEntity.status(problem.getCode()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(problem.getCode(), problem.getMessage()));
    }

    private Long farmerId(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !"FARMER".equals(user.roleCode())) {
            throw new FarmerRecordProblem(403, "当前账号没有操作权限");
        }
        return user.id();
    }

    private <T> ResponseEntity<AuthResult<T>> ok(T data) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(data));
    }
}
