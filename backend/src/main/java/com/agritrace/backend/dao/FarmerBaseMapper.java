package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.FarmerBaseForm;
import com.agritrace.backend.vo.farmer.FarmerBaseRow;
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
public interface FarmerBaseMapper {
    @Select("SELECT COUNT(*) FROM planting_base WHERE owner_id = #{ownerId}")
    long count(@Param("ownerId") Long ownerId);

    @Select("""
            SELECT id, base_name, province, city, district, address, area_mu,
                   description, status, created_at, updated_at
            FROM planting_base
            WHERE owner_id = #{ownerId}
            ORDER BY id DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    @Results(id = "farmerBaseRow", value = {
        @Result(column = "id", property = "id", id = true),
        @Result(column = "base_name", property = "baseName"),
        @Result(column = "province", property = "province"),
        @Result(column = "city", property = "city"),
        @Result(column = "district", property = "district"),
        @Result(column = "address", property = "address"),
        @Result(column = "area_mu", property = "areaMu"),
        @Result(column = "description", property = "description"),
        @Result(column = "status", property = "status"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "updated_at", property = "updatedAt")
    })
    List<FarmerBaseRow> list(@Param("ownerId") Long ownerId,
                             @Param("size") int size, @Param("offset") long offset);

    @Select("""
            SELECT id, base_name, province, city, district, address, area_mu,
                   description, status, created_at, updated_at
            FROM planting_base WHERE id = #{id} AND owner_id = #{ownerId}
            """)
    @ResultMap("farmerBaseRow")
    FarmerBaseRow find(@Param("ownerId") Long ownerId, @Param("id") Long id);

    @Insert("""
            INSERT INTO planting_base
                (owner_id, base_name, province, city, district, address,
                 area_mu, description, status)
            VALUES (#{ownerId}, #{form.baseName}, #{form.province}, #{form.city},
                    #{form.district}, #{form.address}, #{form.areaMu},
                    #{form.description}, 1)
            """)
    int create(@Param("ownerId") Long ownerId, @Param("form") FarmerBaseForm form);

    @Update("""
            UPDATE planting_base
            SET base_name = #{form.baseName}, province = #{form.province},
                city = #{form.city}, district = #{form.district},
                address = #{form.address}, area_mu = #{form.areaMu},
                description = #{form.description}
            WHERE id = #{id} AND owner_id = #{ownerId} AND status = 1
            """)
    int update(@Param("ownerId") Long ownerId, @Param("id") Long id,
               @Param("form") FarmerBaseForm form);

    @Select("""
            SELECT COUNT(*) FROM trace_batch
            WHERE base_id = #{id} AND status IN ('DRAFT', 'SUBMITTED')
            """)
    int activeBatchCount(@Param("id") Long id);

    @Update("""
            UPDATE planting_base SET status = #{status}
            WHERE id = #{id} AND owner_id = #{ownerId}
            """)
    int setStatus(@Param("ownerId") Long ownerId, @Param("id") Long id,
                  @Param("status") int status);
}
