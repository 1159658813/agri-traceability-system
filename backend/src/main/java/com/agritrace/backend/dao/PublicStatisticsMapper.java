package com.agritrace.backend.dao;

import com.agritrace.backend.dto.portal.PublicChartItem;
import com.agritrace.backend.dto.portal.PublicOverview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PublicStatisticsMapper {

    // 只统计通过批次审核的公开信息。没有公开批次时各项均为0。
    @Select("""
        SELECT
          (SELECT COUNT(*) FROM trace_batch t WHERE t.status = 'PUBLISHED') AS publishedBatches,
          (SELECT COUNT(DISTINCT t.product_id) FROM trace_batch t WHERE t.status = 'PUBLISHED') AS productCount,
          (SELECT COUNT(DISTINCT t.base_id) FROM trace_batch t WHERE t.status = 'PUBLISHED') AS baseCount,
          (SELECT COUNT(DISTINCT CONCAT(b.province, '|', b.city))
             FROM trace_batch t JOIN planting_base b ON b.id = t.base_id
             WHERE t.status = 'PUBLISHED') AS regionCount
        """)
    PublicOverview selectOverview();

    @Select("""
        SELECT DATE_FORMAT(t.published_at, '%Y-%m') AS name, COUNT(*) AS value
        FROM trace_batch t
        WHERE t.status = 'PUBLISHED' AND t.published_at >= #{start}
        GROUP BY DATE_FORMAT(t.published_at, '%Y-%m')
        ORDER BY name
        """)
    List<PublicChartItem> selectMonthly(@Param("start") LocalDateTime start);

    @Select("""
        SELECT c.category_name AS name, COUNT(*) AS value
        FROM trace_batch t
        JOIN agri_product p ON p.id = t.product_id
        JOIN product_category c ON c.id = p.category_id
        WHERE t.status = 'PUBLISHED'
        GROUP BY c.id, c.category_name
        ORDER BY value DESC, c.id ASC
        LIMIT 8
        """)
    List<PublicChartItem> selectCategories();

    @Select("""
        SELECT b.province AS name, COUNT(*) AS value
        FROM trace_batch t JOIN planting_base b ON b.id = t.base_id
        WHERE t.status = 'PUBLISHED'
        GROUP BY b.province
        ORDER BY value DESC, b.province ASC
        LIMIT 8
        """)
    List<PublicChartItem> selectRegions();
}
