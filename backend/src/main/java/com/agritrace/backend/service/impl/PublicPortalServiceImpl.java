package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.PublicNoticeMapper;
import com.agritrace.backend.dao.PublicStatisticsMapper;
import com.agritrace.backend.dto.portal.PublicChartItem;
import com.agritrace.backend.dto.portal.PublicNotice;
import com.agritrace.backend.dto.portal.PublicOverview;
import com.agritrace.backend.service.PublicPortalService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicPortalServiceImpl implements PublicPortalService {
    private final PublicStatisticsMapper statisticsMapper;
    private final PublicNoticeMapper noticeMapper;

    public PublicPortalServiceImpl(PublicStatisticsMapper statisticsMapper, PublicNoticeMapper noticeMapper) {
        this.statisticsMapper = statisticsMapper;
        this.noticeMapper = noticeMapper;
    }

    @Override
    public PublicOverview overview() {
        return statisticsMapper.selectOverview();
    }

    @Override
    public List<PublicChartItem> monthlyTrend() {
        YearMonth first = YearMonth.now().minusMonths(5);
        LocalDate start = first.atDay(1);
        Map<String, Long> counts = new HashMap<>();
        for (PublicChartItem item : statisticsMapper.selectMonthly(start.atStartOfDay())) {
            counts.put(item.getName(), item.getValue());
        }

        List<PublicChartItem> result = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String month = first.plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            result.add(new PublicChartItem(month, counts.getOrDefault(month, 0L)));
        }
        return result;
    }

    @Override
    public List<PublicChartItem> categories() { return statisticsMapper.selectCategories(); }

    @Override
    public List<PublicChartItem> regions() { return statisticsMapper.selectRegions(); }

    @Override
    public List<PublicNotice> notices(int limit) { return noticeMapper.selectVisible(limit); }

    @Override
    public PublicNotice notice(long id) { return noticeMapper.selectVisibleById(id); }
}
