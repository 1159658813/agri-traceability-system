package com.agritrace.backend.dto.farmer;

public record FarmerProductForm(Long categoryId, String productName,
                                String variety, String description) {
}
