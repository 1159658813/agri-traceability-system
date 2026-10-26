package com.agritrace.backend.controller;

import com.agritrace.backend.entity.PlantingBase;
import com.agritrace.backend.service.PlantingBaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agritrace.backend.common.ApiResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class PlantingBaseController {
    private final PlantingBaseService plantingBaseService;
    public PlantingBaseController(PlantingBaseService plantingBaseService) {
        this.plantingBaseService = plantingBaseService;
    }
    @GetMapping
    public List<PlantingBase> list() {
        return plantingBaseService.listBases();
    }

    @GetMapping("/page")
    public ApiResponse<IPage<PlantingBase>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {

        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数不正确");
        }

        IPage<PlantingBase> result =
                plantingBaseService.pageBases(pageNum, pageSize);

        return ApiResponse.success(result);
    }
}