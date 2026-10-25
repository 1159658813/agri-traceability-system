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
 * 瀹屾暣鎬ц瘎浠疯?褰
 * </p>
 *
 * @author agri-traceability
 * @since 2026-10-25
 */
@TableName("completeness_result")
public class CompletenessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("batch_id")
    private Long batchId;

    @TableField("rule_version")
    private String ruleVersion;

    @TableField("score")
    private BigDecimal score;

    /**
     * 缂哄け椤圭洰浠ｇ爜鍜屾彁绀轰俊鎭
     */
    @TableField("missing_items")
    private String missingItems;

    @TableField("evaluated_at")
    private LocalDateTime evaluatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getMissingItems() {
        return missingItems;
    }

    public void setMissingItems(String missingItems) {
        this.missingItems = missingItems;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    @Override
    public String toString() {
        return "CompletenessResult{" +
            "id = " + id +
            ", batchId = " + batchId +
            ", ruleVersion = " + ruleVersion +
            ", score = " + score +
            ", missingItems = " + missingItems +
            ", evaluatedAt = " + evaluatedAt +
            "}";
    }
}
