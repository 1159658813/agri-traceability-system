package com.agritrace.backend.vo.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BatchScoreHistoryEntry {
    private Long id;
    private String ruleVersion;
    private BigDecimal score;
    private String missingItemsJson;
    private LocalDateTime evaluatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleVersion() { return ruleVersion; }
    public void setRuleVersion(String ruleVersion) { this.ruleVersion = ruleVersion; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getMissingItemsJson() { return missingItemsJson; }
    public void setMissingItemsJson(String missingItemsJson) { this.missingItemsJson = missingItemsJson; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
