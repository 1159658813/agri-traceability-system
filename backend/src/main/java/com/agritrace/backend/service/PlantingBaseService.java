package com.agritrace.backend.service;

import com.agritrace.backend.entity.PlantingBase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

public interface PlantingBaseService {
    List<PlantingBase> listBases();
    IPage<PlantingBase> pageBases(long pageNum, long pageSize);

}