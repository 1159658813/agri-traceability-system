package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.farmer.FarmerBaseForm;
import com.agritrace.backend.dto.farmer.FarmerBaseStatus;
import com.agritrace.backend.service.FarmerBaseProblem;
import com.agritrace.backend.service.FarmerBaseService;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.FarmerBasePage;
import com.agritrace.backend.vo.farmer.FarmerBaseRow;
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
@RequestMapping("/api/farmer/bases")
public class FarmerBaseController {
    private final FarmerBaseService service;

    public FarmerBaseController(FarmerBaseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<AuthResult<FarmerBasePage>> list(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ok(service.list(ownerId(request), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResult<FarmerBaseRow>> find(
            HttpServletRequest request, @PathVariable Long id) {
        return ok(service.find(ownerId(request), id));
    }

    @PostMapping
    public ResponseEntity<AuthResult<Void>> create(
            HttpServletRequest request, @RequestBody(required = false) FarmerBaseForm form) {
        service.create(ownerId(request), form);
        return ok(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResult<Void>> update(
            HttpServletRequest request, @PathVariable Long id,
            @RequestBody(required = false) FarmerBaseForm form) {
        service.update(ownerId(request), id, form);
        return ok(null);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AuthResult<Void>> changeStatus(
            HttpServletRequest request, @PathVariable Long id,
            @RequestBody(required = false) FarmerBaseStatus form) {
        service.changeStatus(ownerId(request), id, form == null ? null : form.status());
        return ok(null);
    }

    @ExceptionHandler(FarmerBaseProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(FarmerBaseProblem error) {
        return ResponseEntity.status(error.getCode()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(error.getCode(), error.getMessage()));
    }

    private Long ownerId(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !"FARMER".equals(user.roleCode())) {
            throw new FarmerBaseProblem(403, "当前账号没有操作权限");
        }
        return user.id();
    }

    private <T> ResponseEntity<AuthResult<T>> ok(T data) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(data));
    }
}
