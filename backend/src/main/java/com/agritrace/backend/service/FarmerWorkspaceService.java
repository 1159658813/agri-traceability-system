package com.agritrace.backend.service;

import com.agritrace.backend.vo.farmer.FarmerOverview;

public interface FarmerWorkspaceService {
    FarmerOverview overview(Long farmerId);
}
