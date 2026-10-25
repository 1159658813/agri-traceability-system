package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 浜у湴涓庡搧绉嶇壒鑹
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("origin_profile")
public class OriginProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("base_id")
    private Long baseId;

    @TableField("product_id")
    private Long productId;

    @TableField("climate_info")
    private String climateInfo;

    @TableField("soil_info")
    private String soilInfo;

    /**
     * 涓?埇鎬ч?鍛充粙缁嶏紝闈炲叿浣撴壒娆″疄娴嬬粨鏋
     */
    @TableField("flavor_note")
    private String flavorNote;

    /**
     * V1浠ラ?鍙峰垎闅旂畝鍖栫?鐞
     */
    @TableField("feature_tags")
    private String featureTags;

    /**
     * 浠嬬粛淇℃伅鏉ユ簮
     */
    @TableField("information_source")
    private String informationSource;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getClimateInfo() {
        return climateInfo;
    }

    public void setClimateInfo(String climateInfo) {
        this.climateInfo = climateInfo;
    }

    public String getSoilInfo() {
        return soilInfo;
    }

    public void setSoilInfo(String soilInfo) {
        this.soilInfo = soilInfo;
    }

    public String getFlavorNote() {
        return flavorNote;
    }

    public void setFlavorNote(String flavorNote) {
        this.flavorNote = flavorNote;
    }

    public String getFeatureTags() {
        return featureTags;
    }

    public void setFeatureTags(String featureTags) {
        this.featureTags = featureTags;
    }

    public String getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(String informationSource) {
        this.informationSource = informationSource;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "OriginProfile{" +
            "id = " + id +
            ", baseId = " + baseId +
            ", productId = " + productId +
            ", climateInfo = " + climateInfo +
            ", soilInfo = " + soilInfo +
            ", flavorNote = " + flavorNote +
            ", featureTags = " + featureTags +
            ", informationSource = " + informationSource +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
