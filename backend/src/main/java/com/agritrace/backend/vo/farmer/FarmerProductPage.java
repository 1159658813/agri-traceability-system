package com.agritrace.backend.vo.farmer;

import java.util.List;

public record FarmerProductPage(long total, int pageNum, int pageSize,
                                List<FarmerProductRow> records) {
}
