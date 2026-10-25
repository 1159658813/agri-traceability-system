package com.agritrace.backend.service.impl;

import com.agritrace.backend.service.SystemStatusService;
import org.springframework.stereotype.Service;

@Service
public class SystemStatusServiceImpl
        implements SystemStatusService {
    @Override
    public String getSystemStatus() {
        return "农产品产地溯源系统运行正常";
    }
}