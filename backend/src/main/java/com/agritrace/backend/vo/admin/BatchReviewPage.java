package com.agritrace.backend.vo.admin;

import java.util.List;

public record BatchReviewPage(long total, int pageNum, int pageSize,
                              List<BatchReviewRow> records) {
}
