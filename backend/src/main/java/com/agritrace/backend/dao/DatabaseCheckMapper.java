package com.agritrace.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseCheckMapper {
    @Select("SELECT 1")
    Integer checkConnection();
}