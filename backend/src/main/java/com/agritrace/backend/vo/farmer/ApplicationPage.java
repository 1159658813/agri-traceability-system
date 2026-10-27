package com.agritrace.backend.vo.farmer;

import java.util.List;

public record ApplicationPage(List<FarmerApplicationRow> records, long total,
                              int pageNum, int pageSize) {
}
