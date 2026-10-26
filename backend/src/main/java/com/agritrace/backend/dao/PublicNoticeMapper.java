package com.agritrace.backend.dao;
import org.apache.ibatis.annotations.ResultMap;
import com.agritrace.backend.dto.portal.PublicNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Result;

import java.util.List;

@Mapper
public interface PublicNoticeMapper {
    @Results(id = "publicNotice", value = {
        @Result(column = "notice_type", property = "noticeType"),
        @Result(column = "is_top", property = "top"),
        @Result(column = "published_at", property = "publishedAt")
    })
    @Select("""
        SELECT id, title, content, notice_type, is_top, published_at
        FROM sys_notice
        WHERE status = 'PUBLISHED'
          AND published_at <= NOW()
          AND (expires_at IS NULL OR expires_at > NOW())
        ORDER BY is_top DESC, published_at DESC, id DESC
        LIMIT #{limit}
        """)
    List<PublicNotice> selectVisible(@Param("limit") int limit);

    @ResultMap("publicNotice")
    @Select("""
        SELECT id, title, content, notice_type, is_top, published_at
        FROM sys_notice
        WHERE id = #{id} AND status = 'PUBLISHED'
          AND published_at <= NOW()
          AND (expires_at IS NULL OR expires_at > NOW())
        """)
    PublicNotice selectVisibleById(@Param("id") long id);
}
