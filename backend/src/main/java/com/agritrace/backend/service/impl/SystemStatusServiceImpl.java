package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.DatabaseCheckMapper;
import com.agritrace.backend.service.SystemStatusService;
import org.springframework.stereotype.Service;

@Service
public class SystemStatusServiceImpl implements SystemStatusService {
    private final DatabaseCheckMapper databaseCheckMapper;
    public SystemStatusServiceImpl(DatabaseCheckMapper databaseCheckMapper) {
        this.databaseCheckMapper = databaseCheckMapper;
    }
    @Override
    public String getSystemStatus() {
        return "农产品产地溯源系统运行正常";
    }
    @Override
    public boolean isDatabaseConnected() {
        return Integer.valueOf(1).equals(
                databaseCheckMapper.checkConnection()
        );
    }
}