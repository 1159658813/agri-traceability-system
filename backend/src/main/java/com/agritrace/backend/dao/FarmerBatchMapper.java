package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.FarmerBatchForm;
import com.agritrace.backend.vo.farmer.FarmerBatchOption;
import com.agritrace.backend.vo.farmer.FarmerBatchRow;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FarmerBatchMapper {
    @Select("""
            SELECT id, product_name AS label FROM agri_product
            WHERE owner_id = #{farmerId} AND status = 1
            ORDER BY id DESC
            """)
    @Results(id = "batchOption", value = {
        @Result(column = "id", property = "id", id = true),
        @Result(column = "label", property = "label")
    })
    List<FarmerBatchOption> products(@Param("farmerId") Long farmerId);

    @Select("""
            SELECT id, base_name AS label FROM planting_base
            WHERE owner_id = #{farmerId} AND status = 1
            ORDER BY id DESC
            """)
    @ResultMap("batchOption")
    List<FarmerBatchOption> bases(@Param("farmerId") Long farmerId);

    @Select("""
            SELECT COUNT(*) FROM agri_product
            WHERE id = #{id} AND owner_id = #{farmerId} AND status = 1
            """)
    int validProduct(@Param("farmerId") Long farmerId, @Param("id") Long id);

    @Select("""
            SELECT COUNT(*) FROM planting_base
            WHERE id = #{id} AND owner_id = #{farmerId} AND status = 1
            """)
    int validBase(@Param("farmerId") Long farmerId, @Param("id") Long id);

    @Select("SELECT COUNT(*) FROM trace_batch WHERE owner_id = #{farmerId}")
    long count(@Param("farmerId") Long farmerId);

    @Select("""
            SELECT t.id, t.batch_no, t.trace_code, t.product_id, p.product_name,
                   t.base_id, b.base_name, t.planting_date, t.planned_harvest_date,
                   t.estimated_quantity_kg, t.status, t.remark, t.published_at,
                   t.created_at, t.updated_at
            FROM trace_batch t
            JOIN agri_product p ON p.id = t.product_id AND p.owner_id = #{farmerId}
            JOIN planting_base b ON b.id = t.base_id AND b.owner_id = #{farmerId}
            WHERE t.owner_id = #{farmerId}
            ORDER BY t.created_at DESC, t.id DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    @Results(id = "farmerBatchRow", value = {
        @Result(column = "id", property = "id", id = true),
        @Result(column = "batch_no", property = "batchNo"),
        @Result(column = "trace_code", property = "traceCode"),
        @Result(column = "product_id", property = "productId"),
        @Result(column = "product_name", property = "productName"),
        @Result(column = "base_id", property = "baseId"),
        @Result(column = "base_name", property = "baseName"),
        @Result(column = "planting_date", property = "plantingDate"),
        @Result(column = "planned_harvest_date", property = "plannedHarvestDate"),
        @Result(column = "estimated_quantity_kg", property = "estimatedQuantityKg"),
        @Result(column = "status", property = "status"),
        @Result(column = "remark", property = "remark"),
        @Result(column = "published_at", property = "publishedAt"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "updated_at", property = "updatedAt")
    })
    List<FarmerBatchRow> list(@Param("farmerId") Long farmerId,
                              @Param("size") int size, @Param("offset") long offset);

    @Select("""
            SELECT t.id, t.batch_no, t.trace_code, t.product_id, p.product_name,
                   t.base_id, b.base_name, t.planting_date, t.planned_harvest_date,
                   t.estimated_quantity_kg, t.status, t.remark, t.published_at,
                   t.created_at, t.updated_at
            FROM trace_batch t
            JOIN agri_product p ON p.id = t.product_id AND p.owner_id = #{farmerId}
            JOIN planting_base b ON b.id = t.base_id AND b.owner_id = #{farmerId}
            WHERE t.owner_id = #{farmerId} AND t.id = #{id}
            """)
    @ResultMap("farmerBatchRow")
    FarmerBatchRow find(@Param("farmerId") Long farmerId, @Param("id") Long id);

    @Insert("""
            INSERT INTO trace_batch
                (batch_no, trace_code, product_id, base_id, owner_id,
                 planting_date, planned_harvest_date, estimated_quantity_kg,
                 status, remark)
            VALUES (#{batchNo}, #{traceCode}, #{form.productId}, #{form.baseId}, #{farmerId},
                    #{form.plantingDate}, #{form.plannedHarvestDate},
                    #{form.estimatedQuantityKg}, 'DRAFT', #{form.remark})
            """)
    int create(@Param("farmerId") Long farmerId, @Param("batchNo") String batchNo,
               @Param("traceCode") String traceCode, @Param("form") FarmerBatchForm form);

    @Update("""
            UPDATE trace_batch
            SET product_id = #{form.productId}, base_id = #{form.baseId},
                planting_date = #{form.plantingDate},
                planned_harvest_date = #{form.plannedHarvestDate},
                estimated_quantity_kg = #{form.estimatedQuantityKg},
                remark = #{form.remark}, status = 'DRAFT'
            WHERE id = #{id} AND owner_id = #{farmerId}
              AND status IN ('DRAFT', 'REJECTED')
            """)
    int update(@Param("farmerId") Long farmerId, @Param("id") Long id,
               @Param("form") FarmerBatchForm form);

    @Update("""
            UPDATE trace_batch SET status = 'SUBMITTED'
            WHERE id = #{id} AND owner_id = #{farmerId}
              AND status = 'DRAFT' AND planting_date IS NOT NULL
              AND EXISTS (SELECT 1 FROM agri_product p
                          WHERE p.id = trace_batch.product_id
                          AND p.owner_id = #{farmerId} AND p.status = 1)
              AND EXISTS (SELECT 1 FROM planting_base b
                          WHERE b.id = trace_batch.base_id
                          AND b.owner_id = #{farmerId} AND b.status = 1)
            """)
    int submit(@Param("farmerId") Long farmerId, @Param("id") Long id);
}
