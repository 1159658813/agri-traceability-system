package com.agritrace.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FarmerProofMapper {
    @Select("SELECT proof_file_path FROM farmer_application WHERE id = #{id}")
    String findProofPath(@Param("id") Long id);
}
