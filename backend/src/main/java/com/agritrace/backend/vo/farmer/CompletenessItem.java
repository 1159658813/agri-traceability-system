package com.agritrace.backend.vo.farmer;

public record CompletenessItem(String code, String label, int weight, boolean filled, String missingHint) {
}
