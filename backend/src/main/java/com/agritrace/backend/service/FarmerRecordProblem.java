package com.agritrace.backend.service;

public class FarmerRecordProblem extends RuntimeException {
    private final int code;

    public FarmerRecordProblem(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
