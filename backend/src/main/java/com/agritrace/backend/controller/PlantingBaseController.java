package com.agritrace.backend.controller;

import com.agritrace.backend.entity.PlantingBase;
import com.agritrace.backend.service.PlantingBaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}