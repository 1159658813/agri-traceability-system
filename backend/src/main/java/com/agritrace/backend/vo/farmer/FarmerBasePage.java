package com.agritrace.backend.vo.farmer;

import java.util.List;

public record FarmerBasePage(long total, int pageNum, int pageSize, List<FarmerBaseRow> records) {
}
