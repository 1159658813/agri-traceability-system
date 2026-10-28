package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.FarmWorkForm;
import com.agritrace.backend.dto.farmer.HarvestForm;
import com.agritrace.backend.vo.farmer.BatchRecordInfo;
import com.agritrace.backend.vo.farmer.FarmWorkRow;
import com.agritrace.backend.vo.farmer.HarvestRow;
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
public interface FarmerProductionRecordMapper {
    @Select("""
            SELECT t.id, t.batch_no, p.product_name, t.status, t.planting_date
            FROM trace_batch t JOIN agri_product p ON p.id = t.product_id
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            """)
    @Results(id = "recordBatch", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "batch_no", property = "batchNo"),
        @Result(column = "product_name", property = "productName"),
        @Result(column = "status", property = "status"),
        @Result(column = "planting_date", property = "plantingDate")
    })
    BatchRecordInfo findBatch(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId);

    @Select("""
            SELECT t.id, t.batch_no, p.product_name, t.status, t.planting_date
            FROM trace_batch t JOIN agri_product p ON p.id = t.product_id
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            FOR UPDATE
            """)
    @ResultMap("recordBatch")
    BatchRecordInfo lockBatch(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId);

    @Select("""
            SELECT f.id, f.operation_type, f.operated_at, f.description,
                   f.operator_name, f.created_at
            FROM farm_record f JOIN trace_batch t ON t.id = f.batch_id
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            ORDER BY f.operated_at DESC, f.id DESC
            """)
    @Results(id = "farmWork", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "operation_type", property = "operationType"),
        @Result(column = "operated_at", property = "operatedAt"),
        @Result(column = "description", property = "description"),
        @Result(column = "operator_name", property = "operatorName"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<FarmWorkRow> listFarm(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId);

    @Select("""
            SELECT h.id, h.harvested_at, h.quantity_kg, h.grade, h.remark, h.created_at
            FROM harvest_record h JOIN trace_batch t ON t.id = h.batch_id
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            ORDER BY h.harvested_at DESC, h.id DESC
            """)
    @Results(id = "harvestRow", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "harvested_at", property = "harvestedAt"),
        @Result(column = "quantity_kg", property = "quantityKg"),
        @Result(column = "grade", property = "grade"),
        @Result(column = "remark", property = "remark"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<HarvestRow> listHarvest(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId);

    @Insert("""
            INSERT INTO farm_record
                (batch_id, operation_type, operated_at, description, operator_name, created_by)
            VALUES (#{batchId}, #{form.operationType}, #{form.operatedAt},
                    #{form.description}, #{form.operatorName}, #{farmerId})
            """)
    int createFarm(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId,
                   @Param("form") FarmWorkForm form);

    @Update("""
            UPDATE farm_record f JOIN trace_batch t ON t.id = f.batch_id
            SET f.operation_type = #{form.operationType}, f.operated_at = #{form.operatedAt},
                f.description = #{form.description}, f.operator_name = #{form.operatorName}
            WHERE f.id = #{recordId} AND f.batch_id = #{batchId}
              AND t.owner_id = #{farmerId} AND t.status IN ('DRAFT', 'REJECTED')
            """)
    int updateFarm(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId,
                   @Param("recordId") Long recordId, @Param("form") FarmWorkForm form);

    @Insert("""
            INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
            VALUES (#{batchId}, #{form.harvestedAt}, #{form.quantityKg},
                    #{form.grade}, #{form.remark})
            """)
    int createHarvest(@Param("batchId") Long batchId, @Param("form") HarvestForm form);

    @Update("""
            UPDATE harvest_record h JOIN trace_batch t ON t.id = h.batch_id
            SET h.harvested_at = #{form.harvestedAt}, h.quantity_kg = #{form.quantityKg},
                h.grade = #{form.grade}, h.remark = #{form.remark}
            WHERE h.id = #{recordId} AND h.batch_id = #{batchId}
              AND t.owner_id = #{farmerId} AND t.status IN ('DRAFT', 'REJECTED')
            """)
    int updateHarvest(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId,
                      @Param("recordId") Long recordId, @Param("form") HarvestForm form);
}
