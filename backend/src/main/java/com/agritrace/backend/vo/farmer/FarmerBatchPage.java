package com.agritrace.backend.vo.farmer;

import java.util.List;

public record FarmerBatchPage(long total, int pageNum, int pageSize,
                              List<FarmerBatchRow> records) {
}
