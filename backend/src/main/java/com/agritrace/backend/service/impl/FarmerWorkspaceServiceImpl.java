package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerWorkspaceMapper;
import com.agritrace.backend.service.FarmerWorkspaceService;
import com.agritrace.backend.vo.farmer.FarmerOverview;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerWorkspaceServiceImpl implements FarmerWorkspaceService {
    private final FarmerWorkspaceMapper mapper;

    public FarmerWorkspaceServiceImpl(FarmerWorkspaceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerOverview overview(Long farmerId) {
        return new FarmerOverview(
                mapper.countBases(farmerId),
                mapper.countProducts(farmerId),
                mapper.countBatches(farmerId),
                mapper.countPublished(farmerId),
                mapper.countPending(farmerId),
                mapper.recentBatches(farmerId)
        );
    }
}
