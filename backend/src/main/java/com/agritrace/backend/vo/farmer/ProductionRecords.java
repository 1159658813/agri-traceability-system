package com.agritrace.backend.vo.farmer;

import java.util.List;

public record ProductionRecords(BatchRecordInfo batch,
                                List<FarmWorkRow> farmRecords,
                                List<HarvestRow> harvestRecords) {
}
