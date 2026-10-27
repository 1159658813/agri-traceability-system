package com.agritrace.backend.dao;

import com.agritrace.backend.vo.farmer.FarmerBatchSummary;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmerWorkspaceMapper {
    @Select("SELECT COUNT(*) FROM planting_base WHERE owner_id = #{ownerId}")
    int countBases(@Param("ownerId") Long ownerId);

    @Select("SELECT COUNT(*) FROM agri_product WHERE owner_id = #{ownerId}")
    int countProducts(@Param("ownerId") Long ownerId);

    @Select("SELECT COUNT(*) FROM trace_batch WHERE owner_id = #{ownerId}")
    int countBatches(@Param("ownerId") Long ownerId);

    @Select("SELECT COUNT(*) FROM trace_batch WHERE owner_id = #{ownerId} AND status = 'PUBLISHED'")
    int countPublished(@Param("ownerId") Long ownerId);

    @Select("SELECT COUNT(*) FROM trace_batch WHERE owner_id = #{ownerId} AND status = 'SUBMITTED'")
    int countPending(@Param("ownerId") Long ownerId);

    @Select("""
            SELECT t.id, t.batch_no, p.product_name, b.base_name, t.status, t.created_at
            FROM trace_batch t
            JOIN agri_product p ON p.id = t.product_id AND p.owner_id = #{ownerId}
            JOIN planting_base b ON b.id = t.base_id AND b.owner_id = #{ownerId}
            WHERE t.owner_id = #{ownerId}
            ORDER BY t.created_at DESC, t.id DESC
            LIMIT 10
            """)
    @Results({
        @Result(column = "id", property = "id", id = true),
        @Result(column = "batch_no", property = "batchNo"),
        @Result(column = "product_name", property = "productName"),
        @Result(column = "base_name", property = "baseName"),
        @Result(column = "status", property = "status"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<FarmerBatchSummary> recentBatches(@Param("ownerId") Long ownerId);
}
