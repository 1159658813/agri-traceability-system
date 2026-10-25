package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.PlantingBaseMapper;
import com.agritrace.backend.entity.PlantingBase;
import com.agritrace.backend.service.PlantingBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantingBaseServiceImpl implements PlantingBaseService {
    private final PlantingBaseMapper plantingBaseMapper;
    public PlantingBaseServiceImpl(PlantingBaseMapper plantingBaseMapper) {
        this.plantingBaseMapper = plantingBaseMapper;
    }
    @Override
    public List<PlantingBase> listBases() {
        LambdaQueryWrapper<PlantingBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PlantingBase::getId);
        return plantingBaseMapper.selectList(wrapper);
    }
}