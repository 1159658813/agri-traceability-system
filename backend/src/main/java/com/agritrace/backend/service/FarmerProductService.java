package com.agritrace.backend.service;

import com.agritrace.backend.dto.farmer.FarmerProductForm;
import com.agritrace.backend.vo.farmer.FarmerProductCategory;
import com.agritrace.backend.vo.farmer.FarmerProductPage;
import com.agritrace.backend.vo.farmer.FarmerProductRow;
import java.util.List;

public interface FarmerProductService {
    List<FarmerProductCategory> categories();
    FarmerProductPage list(Long farmerId, int pageNum, int pageSize);
    FarmerProductRow find(Long farmerId, Long id);
    void create(Long farmerId, FarmerProductForm form);
    void update(Long farmerId, Long id, FarmerProductForm form);
    void changeStatus(Long farmerId, Long id, Integer status);
}
