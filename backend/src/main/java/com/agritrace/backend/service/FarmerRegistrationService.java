package com.agritrace.backend.service;

import com.agritrace.backend.dto.farmer.FarmerApplyForm;

public interface FarmerRegistrationService {
    void apply(FarmerApplyForm form);
}
