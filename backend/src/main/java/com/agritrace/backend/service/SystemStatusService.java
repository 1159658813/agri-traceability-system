package com.agritrace.backend.service;

public interface SystemStatusService {
    String getSystemStatus();
    boolean isDatabaseConnected();
}