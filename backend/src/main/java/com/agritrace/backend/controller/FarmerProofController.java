package com.agritrace.backend.controller;

import com.agritrace.backend.dao.FarmerProofMapper;
import com.agritrace.backend.service.PrivateProofStorage;
import com.agritrace.backend.service.RegistrationProblem;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/farmer-applications")
public class FarmerProofController {
    private final FarmerProofMapper mapper;
    private final PrivateProofStorage storage;

    public FarmerProofController(FarmerProofMapper mapper, PrivateProofStorage storage) {
        this.mapper = mapper;
        this.storage = storage;
    }

    // 此接口受 AuthInterceptor 的 ADMIN 会话校验保护，且不公开真实存储路径。
    @GetMapping("/{id}/proof")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) throws IOException {
        if (id == null || id < 1) throw new RegistrationProblem(404, "文件不存在");
        Path path = storage.find(mapper.findProofPath(id));
        String ext = path.getFileName().toString().replaceAll("^.*\\.", "");
        MediaType type = switch (ext) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "png" -> MediaType.IMAGE_PNG;
            default -> MediaType.IMAGE_JPEG;
        };
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=farmer-proof-" + id + "." + ext)
                .header("X-Content-Type-Options", "nosniff")
                .contentType(type)
                .body(new ByteArrayResource(Files.readAllBytes(path)));
    }

    @ExceptionHandler(RegistrationProblem.class)
    public ResponseEntity<String> error(RegistrationProblem ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}
