package com.agritrace.backend.service;

import com.agritrace.backend.dto.portal.PublicChartItem;
import com.agritrace.backend.dto.portal.PublicNotice;
import com.agritrace.backend.dto.portal.PublicOverview;
import java.util.List;

public interface PublicPortalService {
    PublicOverview overview();
    List<PublicChartItem> monthlyTrend();
    List<PublicChartItem> categories();
    List<PublicChartItem> regions();
    List<PublicNotice> notices(int limit);
    PublicNotice notice(long id);
}
