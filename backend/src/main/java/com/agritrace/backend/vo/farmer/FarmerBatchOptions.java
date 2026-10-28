package com.agritrace.backend.vo.farmer;

import java.util.List;

public record FarmerBatchOptions(List<FarmerBatchOption> products,
                                 List<FarmerBatchOption> bases) {
}
