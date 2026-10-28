package com.agritrace.backend.dto.farmer;

import java.math.BigDecimal;

public record FarmerBaseForm(
        String baseName, String province, String city, String district,
        String address, BigDecimal areaMu, String description) {
}
