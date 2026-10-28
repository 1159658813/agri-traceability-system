package com.agritrace.backend.service;

public class FarmerProductProblem extends RuntimeException {
    private final int code;

    public FarmerProductProblem(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
