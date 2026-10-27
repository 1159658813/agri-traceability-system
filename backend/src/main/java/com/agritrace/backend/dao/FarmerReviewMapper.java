package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.ApplicationLock;
import com.agritrace.backend.dto.farmer.FarmerAccountLock;
import com.agritrace.backend.vo.farmer.FarmerApplicationRow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FarmerReviewMapper {
    @Select("""
            SELECT a.id, u.username, a.applicant_type, a.organization_name,
                   a.contact_name, a.contact_phone, a.farming_location,
                   a.proof_type, a.application_note, a.review_status,
                   a.review_comment, a.submitted_at, a.reviewed_at,
                   IF(a.proof_type = 'DEMO_PLACEHOLDER'
                      AND a.proof_file_path LIKE 'DEMO_ONLY/NO_REAL_FILE/%', 1, 0) AS demo_proof
              FROM farmer_application a
              JOIN sys_user u ON u.id = a.applicant_id
             WHERE (#{status} = 'ALL' OR a.review_status = #{status})
             ORDER BY a.submitted_at DESC, a.id DESC
             LIMIT #{limit} OFFSET #{offset}
            """)
    @Results(id = "farmerApplicationRow", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "username", property = "username"),
        @Result(column = "applicant_type", property = "applicantType"),
        @Result(column = "organization_name", property = "organizationName"),
        @Result(column = "contact_name", property = "contactName"),
        @Result(column = "contact_phone", property = "contactPhone"),
        @Result(column = "farming_location", property = "farmingLocation"),
        @Result(column = "proof_type", property = "proofType"),
        @Result(column = "application_note", property = "applicationNote"),
        @Result(column = "review_status", property = "reviewStatus"),
        @Result(column = "review_comment", property = "reviewComment"),
        @Result(column = "submitted_at", property = "submittedAt"),
        @Result(column = "reviewed_at", property = "reviewedAt"),
        @Result(column = "demo_proof", property = "demoProof")
    })
    List<FarmerApplicationRow> findPage(@Param("status") String status,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    @Select("""
            SELECT COUNT(*) FROM farmer_application
            WHERE (#{status} = 'ALL' OR review_status = #{status})
            """)
    long countByStatus(@Param("status") String status);

    @Select("""
            SELECT id, applicant_id, review_status, proof_type, proof_file_path
              FROM farmer_application WHERE id = #{id} FOR UPDATE
            """)
    @Results({
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "applicant_id", property = "applicantId"),
        @Result(column = "review_status", property = "reviewStatus"),
        @Result(column = "proof_type", property = "proofType"),
        @Result(column = "proof_file_path", property = "proofFilePath")
    })
    ApplicationLock lockApplication(@Param("id") Long id);

    @Select("SELECT role_code, status FROM sys_user WHERE id = #{id} FOR UPDATE")
    @Results({
        @Result(column = "role_code", property = "roleCode"),
        @Result(column = "status", property = "status")
    })
    FarmerAccountLock lockFarmer(@Param("id") Long id);

    @Update("""
            UPDATE farmer_application
               SET review_status = #{decision}, reviewer_id = #{reviewerId},
                   review_comment = #{comment}, reviewed_at = NOW()
             WHERE id = #{id} AND review_status = 'PENDING'
            """)
    int updateReview(@Param("id") Long id,
                     @Param("decision") String decision,
                     @Param("reviewerId") Long reviewerId,
                     @Param("comment") String comment);

    @Update("""
            UPDATE sys_user SET status = 1
            WHERE id = #{id} AND role_code = 'FARMER' AND status = 0
            """)
    int activateFarmer(@Param("id") Long id);
}
