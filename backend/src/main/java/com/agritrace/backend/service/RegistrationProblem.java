package com.agritrace.backend.service;

public class RegistrationProblem extends RuntimeException {
    private final int status;

    public RegistrationProblem(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
