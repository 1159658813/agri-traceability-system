package com.agritrace.backend.vo.farmer;

import java.util.List;

public record FarmerOverview(int baseCount, int productCount, int batchCount,
                             int publishedCount, int pendingCount,
                             List<FarmerBatchSummary> recentBatches) {
}
