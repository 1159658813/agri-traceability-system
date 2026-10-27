package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.FarmerApplyForm;
import com.agritrace.backend.dto.farmer.NewFarmerAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmerRegistrationMapper {
    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username}")
    int countUsername(@Param("username") String username);

    @Insert("""
            INSERT INTO sys_user (username, password_hash, display_name, role_code, status)
            VALUES (#{username}, #{passwordHash}, #{displayName}, 'FARMER', 0)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createAccount(NewFarmerAccount account);

    @Insert("""
            INSERT INTO farmer_application
              (applicant_id, applicant_type, organization_name, contact_name,
               contact_phone, farming_location, proof_type, proof_file_path, application_note,
               review_status)
            VALUES (#{userId}, #{form.applicantType}, #{form.organizationName},
                    #{form.contactName}, #{form.contactPhone}, #{form.farmingLocation},
                    #{form.proofType}, #{proofPath}, #{form.applicationNote}, 'PENDING')
            """)
    int createApplication(@Param("userId") Long userId,
                          @Param("form") FarmerApplyForm form,
                          @Param("proofPath") String proofPath);
}
