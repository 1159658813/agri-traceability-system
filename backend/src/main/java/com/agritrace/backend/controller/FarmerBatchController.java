package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.farmer.FarmerBatchForm;
import com.agritrace.backend.service.FarmerBatchProblem;
import com.agritrace.backend.service.FarmerBatchService;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.FarmerBatchOptions;
import com.agritrace.backend.vo.farmer.FarmerBatchPage;
import com.agritrace.backend.vo.farmer.FarmerBatchRow;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer/batches")
public class FarmerBatchController {
    private final FarmerBatchService service;

    public FarmerBatchController(FarmerBatchService service) {
        this.service = service;
    }

    @GetMapping("/options")
    public ResponseEntity<AuthResult<FarmerBatchOptions>> options(HttpServletRequest request) {
        return ok(service.options(ownerId(request)));
    }

    @GetMapping
    public ResponseEntity<AuthResult<FarmerBatchPage>> list(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ok(service.list(ownerId(request), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResult<FarmerBatchRow>> find(HttpServletRequest request,
            @PathVariable Long id) {
        return ok(service.find(ownerId(request), id));
    }

    @PostMapping
    public ResponseEntity<AuthResult<Void>> create(HttpServletRequest request,
            @RequestBody(required = false) FarmerBatchForm form) {
        service.create(ownerId(request), form);
        return ok(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResult<Void>> update(HttpServletRequest request,
            @PathVariable Long id, @RequestBody(required = false) FarmerBatchForm form) {
        service.update(ownerId(request), id, form);
        return ok(null);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<AuthResult<Void>> submit(HttpServletRequest request,
            @PathVariable Long id) {
        service.submit(ownerId(request), id);
        return ok(null);
    }

    @ExceptionHandler(FarmerBatchProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(FarmerBatchProblem problem) {
        return ResponseEntity.status(problem.getCode()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(problem.getCode(), problem.getMessage()));
    }

    private Long ownerId(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !"FARMER".equals(user.roleCode())) {
            throw new FarmerBatchProblem(403, "当前账号没有操作权限");
        }
        return user.id();
    }

    private <T> ResponseEntity<AuthResult<T>> ok(T data) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(data));
    }
}
