package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.CirculationForm;
import com.agritrace.backend.dto.farmer.QualityInspectionForm;
import com.agritrace.backend.vo.farmer.CirculationRow;
import com.agritrace.backend.vo.farmer.QualityInspectionRow;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FarmerSupplementalRecordMapper {
    @Select("""
            SELECT q.id, q.inspected_at, q.inspection_item, q.inspection_org,
                   q.report_no, q.conclusion, q.remark, q.created_at
            FROM quality_inspection q JOIN trace_batch t ON t.id = q.batch_id
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            ORDER BY q.inspected_at DESC, q.id DESC
            """)
    @Results(id = "qualityRows", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "inspected_at", property = "inspectedAt"),
        @Result(column = "inspection_item", property = "inspectionItem"),
        @Result(column = "inspection_org", property = "inspectionOrg"),
        @Result(column = "report_no", property = "reportNo"),
        @Result(column = "conclusion", property = "conclusion"),
        @Result(column = "remark", property = "remark"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<QualityInspectionRow> listQuality(@Param("farmerId") Long farmerId,
                                          @Param("batchId") Long batchId);

    @Select("""
            SELECT c.id, c.event_type, c.event_at, c.from_place, c.to_place,
                   c.operator_name, c.logistics_no, c.remark, c.created_at
            FROM circulation_record c JOIN trace_batch t ON t.id = c.batch_id
            WHERE t.id = #{batchId} AND t.owner_id = #{farmerId}
            ORDER BY c.event_at ASC, c.id ASC
            """)
    @Results(id = "circulationRows", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "event_type", property = "eventType"),
        @Result(column = "event_at", property = "eventAt"),
        @Result(column = "from_place", property = "fromPlace"),
        @Result(column = "to_place", property = "toPlace"),
        @Result(column = "operator_name", property = "operatorName"),
        @Result(column = "logistics_no", property = "logisticsNo"),
        @Result(column = "remark", property = "remark"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<CirculationRow> listCirculation(@Param("farmerId") Long farmerId,
                                        @Param("batchId") Long batchId);

    @Insert("""
            INSERT INTO quality_inspection
                (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
            VALUES (#{batchId}, #{form.inspectedAt}, #{form.inspectionItem},
                    #{form.inspectionOrg}, #{form.reportNo}, #{form.conclusion}, #{form.remark})
            """)
    int createQuality(@Param("batchId") Long batchId, @Param("form") QualityInspectionForm form);

    @Update("""
            UPDATE quality_inspection q JOIN trace_batch t ON t.id = q.batch_id
            SET q.inspected_at = #{form.inspectedAt}, q.inspection_item = #{form.inspectionItem},
                q.inspection_org = #{form.inspectionOrg}, q.report_no = #{form.reportNo},
                q.conclusion = #{form.conclusion}, q.remark = #{form.remark}
            WHERE q.id = #{recordId} AND q.batch_id = #{batchId}
              AND t.owner_id = #{farmerId} AND t.status IN ('DRAFT', 'REJECTED')
            """)
    int updateQuality(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId,
                      @Param("recordId") Long recordId, @Param("form") QualityInspectionForm form);

    @Insert("""
            INSERT INTO circulation_record
                (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
            VALUES (#{batchId}, #{form.eventType}, #{form.eventAt}, #{form.fromPlace},
                    #{form.toPlace}, #{form.operatorName}, #{form.logisticsNo}, #{form.remark})
            """)
    int createCirculation(@Param("batchId") Long batchId, @Param("form") CirculationForm form);

    @Update("""
            UPDATE circulation_record c JOIN trace_batch t ON t.id = c.batch_id
            SET c.event_type = #{form.eventType}, c.event_at = #{form.eventAt},
                c.from_place = #{form.fromPlace}, c.to_place = #{form.toPlace},
                c.operator_name = #{form.operatorName}, c.logistics_no = #{form.logisticsNo},
                c.remark = #{form.remark}
            WHERE c.id = #{recordId} AND c.batch_id = #{batchId}
              AND t.owner_id = #{farmerId} AND t.status IN ('DRAFT', 'REJECTED')
            """)
    int updateCirculation(@Param("farmerId") Long farmerId, @Param("batchId") Long batchId,
                          @Param("recordId") Long recordId, @Param("form") CirculationForm form);
}
