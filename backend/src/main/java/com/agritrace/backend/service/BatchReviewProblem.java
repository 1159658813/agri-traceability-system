package com.agritrace.backend.service;

public class BatchReviewProblem extends RuntimeException {
    private final int status;

    public BatchReviewProblem(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
