package com.agritrace.backend.vo.admin;

import java.math.BigDecimal;

public class CompletenessRuleConfig {
    private String ruleKey;
    private String ruleName;
    private BigDecimal weight;
    private boolean required;

    public String getRuleKey() { return ruleKey; }
    public void setRuleKey(String ruleKey) { this.ruleKey = ruleKey; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
