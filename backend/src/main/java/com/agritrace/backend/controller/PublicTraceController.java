package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.service.PublicTraceProblem;
import com.agritrace.backend.service.PublicTraceService;
import com.agritrace.backend.vo.trace.PublicTraceDetail;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/traces")
public class PublicTraceController {
    private final PublicTraceService service;

    public PublicTraceController(PublicTraceService service) {
        this.service = service;
    }

    @GetMapping("/{traceCode}")
    public ResponseEntity<AuthResult<PublicTraceDetail>> get(@PathVariable String traceCode) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(service.get(traceCode)));
    }

    @ExceptionHandler(PublicTraceProblem.class)
    public ResponseEntity<AuthResult<Void>> fail(PublicTraceProblem error) {
        return ResponseEntity.status(error.getStatus()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(error.getStatus(), error.getMessage()));
    }
}
