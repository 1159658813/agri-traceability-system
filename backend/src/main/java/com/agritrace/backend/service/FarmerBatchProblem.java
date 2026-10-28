package com.agritrace.backend.service;

public class FarmerBatchProblem extends RuntimeException {
    private final int code;

    public FarmerBatchProblem(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
