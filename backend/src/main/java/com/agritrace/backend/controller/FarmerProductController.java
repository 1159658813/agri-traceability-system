package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.config.AuthSession;
import com.agritrace.backend.dto.farmer.FarmerProductForm;
import com.agritrace.backend.dto.farmer.FarmerProductStatus;
import com.agritrace.backend.service.FarmerProductProblem;
import com.agritrace.backend.service.FarmerProductService;
import com.agritrace.backend.vo.auth.LoginUser;
import com.agritrace.backend.vo.farmer.FarmerProductCategory;
import com.agritrace.backend.vo.farmer.FarmerProductPage;
import com.agritrace.backend.vo.farmer.FarmerProductRow;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
@RequestMapping("/api/farmer/products")
public class FarmerProductController {
    private final FarmerProductService service;

    public FarmerProductController(FarmerProductService service) {
        this.service = service;
    }

    @GetMapping("/categories")
    public ResponseEntity<AuthResult<List<FarmerProductCategory>>> categories(HttpServletRequest request) {
        ownerId(request);
        return ok(service.categories());
    }

    @GetMapping
    public ResponseEntity<AuthResult<FarmerProductPage>> list(HttpServletRequest request,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ok(service.list(ownerId(request), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResult<FarmerProductRow>> find(HttpServletRequest request,
            @PathVariable Long id) {
        return ok(service.find(ownerId(request), id));
    }

    @PostMapping
    public ResponseEntity<AuthResult<Void>> create(HttpServletRequest request,
            @RequestBody(required = false) FarmerProductForm form) {
        service.create(ownerId(request), form);
        return ok(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResult<Void>> update(HttpServletRequest request,
            @PathVariable Long id, @RequestBody(required = false) FarmerProductForm form) {
        service.update(ownerId(request), id, form);
        return ok(null);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AuthResult<Void>> changeStatus(HttpServletRequest request,
            @PathVariable Long id, @RequestBody(required = false) FarmerProductStatus form) {
        service.changeStatus(ownerId(request), id, form == null ? null : form.status());
        return ok(null);
    }

    @ExceptionHandler(FarmerProductProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(FarmerProductProblem problem) {
        return ResponseEntity.status(problem.getCode()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(problem.getCode(), problem.getMessage()));
    }

    private Long ownerId(HttpServletRequest request) {
        LoginUser user = AuthSession.getUser(request.getSession(false));
        if (user == null || !"FARMER".equals(user.roleCode())) {
            throw new FarmerProductProblem(403, "当前账号没有操作权限");
        }
        return user.id();
    }

    private <T> ResponseEntity<AuthResult<T>> ok(T data) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(data));
    }
}
