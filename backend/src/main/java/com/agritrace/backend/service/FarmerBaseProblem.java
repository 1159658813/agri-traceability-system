package com.agritrace.backend.service;

public class FarmerBaseProblem extends RuntimeException {
    private final int code;

    public FarmerBaseProblem(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
