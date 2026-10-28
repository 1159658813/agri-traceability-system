package com.agritrace.backend.dao;

import com.agritrace.backend.dto.farmer.FarmerProductForm;
import com.agritrace.backend.vo.farmer.FarmerProductCategory;
import com.agritrace.backend.vo.farmer.FarmerProductRow;
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
public interface FarmerProductMapper {
    @Select("""
            SELECT id, category_name FROM product_category
            WHERE status = 1 ORDER BY sort_order ASC, id ASC
            """)
    @Results(id = "farmerProductCategory", value = {
        @Result(column = "id", property = "id", id = true),
        @Result(column = "category_name", property = "categoryName")
    })
    List<FarmerProductCategory> categories();

    @Select("SELECT status FROM product_category WHERE id = #{id}")
    Integer categoryStatus(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM agri_product WHERE owner_id = #{farmerId}")
    long count(@Param("farmerId") Long farmerId);

    @Select("""
            SELECT p.id, p.category_id, c.category_name, p.product_name,
                   p.variety, p.description, p.status, p.created_at, p.updated_at
            FROM agri_product p
            JOIN product_category c ON c.id = p.category_id
            WHERE p.owner_id = #{farmerId}
            ORDER BY p.id DESC LIMIT #{size} OFFSET #{offset}
            """)
    @Results(id = "farmerProductRow", value = {
        @Result(column = "id", property = "id", id = true),
        @Result(column = "category_id", property = "categoryId"),
        @Result(column = "category_name", property = "categoryName"),
        @Result(column = "product_name", property = "productName"),
        @Result(column = "variety", property = "variety"),
        @Result(column = "description", property = "description"),
        @Result(column = "status", property = "status"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "updated_at", property = "updatedAt")
    })
    List<FarmerProductRow> list(@Param("farmerId") Long farmerId,
                                @Param("size") int size, @Param("offset") long offset);

    @Select("""
            SELECT p.id, p.category_id, c.category_name, p.product_name,
                   p.variety, p.description, p.status, p.created_at, p.updated_at
            FROM agri_product p JOIN product_category c ON c.id = p.category_id
            WHERE p.id = #{id} AND p.owner_id = #{farmerId}
            """)
    @ResultMap("farmerProductRow")
    FarmerProductRow find(@Param("farmerId") Long farmerId, @Param("id") Long id);

    @Insert("""
            INSERT INTO agri_product
                (owner_id, category_id, product_name, variety, description, status)
            VALUES (#{farmerId}, #{form.categoryId}, #{form.productName},
                    #{form.variety}, #{form.description}, 1)
            """)
    int create(@Param("farmerId") Long farmerId, @Param("form") FarmerProductForm form);

    @Update("""
            UPDATE agri_product
            SET category_id = #{form.categoryId}, product_name = #{form.productName},
                variety = #{form.variety}, description = #{form.description}
            WHERE id = #{id} AND owner_id = #{farmerId} AND status = 1
            """)
    int update(@Param("farmerId") Long farmerId, @Param("id") Long id,
               @Param("form") FarmerProductForm form);

    @Select("""
            SELECT COUNT(*) FROM trace_batch
            WHERE product_id = #{id} AND owner_id = #{farmerId}
              AND status IN ('DRAFT', 'SUBMITTED')
            """)
    int activeBatchCount(@Param("farmerId") Long farmerId, @Param("id") Long id);

    @Update("""
            UPDATE agri_product SET status = #{status}
            WHERE id = #{id} AND owner_id = #{farmerId}
            """)
    int setStatus(@Param("farmerId") Long farmerId, @Param("id") Long id,
                  @Param("status") int status);
}
