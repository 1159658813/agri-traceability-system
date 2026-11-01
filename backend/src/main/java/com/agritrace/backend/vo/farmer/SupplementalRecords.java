package com.agritrace.backend.vo.farmer;

import java.util.List;

public record SupplementalRecords(List<QualityInspectionRow> qualityRecords,
                                  List<CirculationRow> circulationRecords) {
}
