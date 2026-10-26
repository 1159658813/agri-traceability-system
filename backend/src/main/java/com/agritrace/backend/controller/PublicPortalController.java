package com.agritrace.backend.controller;

import com.agritrace.backend.common.ApiResponse;
import com.agritrace.backend.dto.portal.PublicChartItem;
import com.agritrace.backend.dto.portal.PublicNotice;
import com.agritrace.backend.dto.portal.PublicOverview;
import com.agritrace.backend.service.PublicPortalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicPortalController {
    private final PublicPortalService portalService;

    public PublicPortalController(PublicPortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/stats/overview")
    public ApiResponse<PublicOverview> overview() { return ApiResponse.success(portalService.overview()); }

    @GetMapping("/stats/trend")
    public ApiResponse<List<PublicChartItem>> trend() { return ApiResponse.success(portalService.monthlyTrend()); }

    @GetMapping("/stats/categories")
    public ApiResponse<List<PublicChartItem>> categories() { return ApiResponse.success(portalService.categories()); }

    @GetMapping("/stats/regions")
    public ApiResponse<List<PublicChartItem>> regions() { return ApiResponse.success(portalService.regions()); }

    @GetMapping("/notices")
    public ApiResponse<List<PublicNotice>> notices(@RequestParam(defaultValue = "5") int limit) {
        if (limit < 1 || limit > 20) throw new IllegalArgumentException("limit必须在1到20之间");
        return ApiResponse.success(portalService.notices(limit));
    }

    @GetMapping("/notices/{id}")
    public ResponseEntity<ApiResponse<PublicNotice>> notice(@PathVariable long id) {
        if (id <= 0) return ResponseEntity.badRequest().body(ApiResponse.error(400, "公告编号无效"));
        PublicNotice notice = portalService.notice(id);
        if (notice == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "公告不存在或尚未发布"));
        }
        return ResponseEntity.ok(ApiResponse.success(notice));
    }
}
