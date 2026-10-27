package com.agritrace.backend.service;

public class ReviewProblem extends RuntimeException {
    private final int status;

    public ReviewProblem(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
