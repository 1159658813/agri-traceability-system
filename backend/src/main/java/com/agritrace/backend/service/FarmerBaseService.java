package com.agritrace.backend.service;

import com.agritrace.backend.dto.farmer.FarmerBaseForm;
import com.agritrace.backend.vo.farmer.FarmerBasePage;
import com.agritrace.backend.vo.farmer.FarmerBaseRow;

public interface FarmerBaseService {
    FarmerBasePage list(Long ownerId, int pageNum, int pageSize);
    FarmerBaseRow find(Long ownerId, Long id);
    void create(Long ownerId, FarmerBaseForm form);
    void update(Long ownerId, Long id, FarmerBaseForm form);
    void changeStatus(Long ownerId, Long id, Integer status);
}
