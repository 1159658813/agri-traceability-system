package com.agritrace.backend.service;

public class PublicTraceProblem extends RuntimeException {
    private final int status;

    public PublicTraceProblem(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
