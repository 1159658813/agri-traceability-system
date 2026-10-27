package com.agritrace.backend.controller;

import com.agritrace.backend.common.AuthResult;
import com.agritrace.backend.dto.farmer.FarmerApplyForm;
import com.agritrace.backend.service.FarmerRegistrationService;
import com.agritrace.backend.service.RegistrationProblem;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestController
@RequestMapping("/api/public/farmer-applications")
public class FarmerRegistrationController {
    private final FarmerRegistrationService registrationService;

    public FarmerRegistrationController(FarmerRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResult<Void>> apply(@ModelAttribute FarmerApplyForm form) {
        registrationService.apply(form);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(AuthResult.ok(null));
    }

    @ExceptionHandler(RegistrationProblem.class)
    public ResponseEntity<AuthResult<Void>> handle(RegistrationProblem ex) {
        return ResponseEntity.status(ex.getStatus()).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<AuthResult<Void>> tooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(413).cacheControl(CacheControl.noStore())
                .body(AuthResult.error(413, "上传文件不能超过5MB"));
    }
}
