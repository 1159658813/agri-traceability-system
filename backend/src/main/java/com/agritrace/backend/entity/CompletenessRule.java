package com.agritrace.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 婧?簮淇℃伅瀹屾暣鎬ц?鍒
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("completeness_rule")
public class CompletenessRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("rule_key")
    private String ruleKey;

    @TableField("stage_code")
    private String stageCode;

    @TableField("rule_name")
    private String ruleName;

    @TableField("weight")
    private BigDecimal weight;

    @TableField("required_flag")
    private Byte requiredFlag;

    @TableField("enabled")
    private Byte enabled;

    @TableField("rule_description")
    private String ruleDescription;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public void setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Byte getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(Byte requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public Byte getEnabled() {
        return enabled;
    }

    public void setEnabled(Byte enabled) {
        this.enabled = enabled;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "CompletenessRule{" +
            "id = " + id +
            ", ruleKey = " + ruleKey +
            ", stageCode = " + stageCode +
            ", ruleName = " + ruleName +
            ", weight = " + weight +
            ", requiredFlag = " + requiredFlag +
            ", enabled = " + enabled +
            ", ruleDescription = " + ruleDescription +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
