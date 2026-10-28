package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.farmer.FarmWorkForm;
import com.agritrace.backend.dto.farmer.HarvestForm;
import com.agritrace.backend.service.FarmerProductionRecordService;
import com.agritrace.backend.service.FarmerRecordProblem;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.ProductionRecords;
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
public class FarmerProductionRecordController {
    private final FarmerProductionRecordService service;

    public FarmerProductionRecordController(FarmerProductionRecordService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<AuthResult<ProductionRecords>> list(HttpServletRequest request,
            @PathVariable Long batchId) {
        return ok(service.list(farmerId(request), batchId));
    }

    @PostMapping("/farm")
    public ResponseEntity<AuthResult<Void>> createFarm(HttpServletRequest request,
            @PathVariable Long batchId, @RequestBody(required = false) FarmWorkForm form) {
        service.createFarm(farmerId(request), batchId, form);
        return ok(null);
    }

    @PutMapping("/farm/{recordId}")
    public ResponseEntity<AuthResult<Void>> updateFarm(HttpServletRequest request,
            @PathVariable Long batchId, @PathVariable Long recordId,
            @RequestBody(required = false) FarmWorkForm form) {
        service.updateFarm(farmerId(request), batchId, recordId, form);
        return ok(null);
    }

    @PostMapping("/harvest")
    public ResponseEntity<AuthResult<Void>> createHarvest(HttpServletRequest request,
            @PathVariable Long batchId, @RequestBody(required = false) HarvestForm form) {
        service.createHarvest(farmerId(request), batchId, form);
        return ok(null);
    }

    @PutMapping("/harvest/{recordId}")
    public ResponseEntity<AuthResult<Void>> updateHarvest(HttpServletRequest request,
            @PathVariable Long batchId, @PathVariable Long recordId,
            @RequestBody(required = false) HarvestForm form) {
        service.updateHarvest(farmerId(request), batchId, recordId, form);
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
