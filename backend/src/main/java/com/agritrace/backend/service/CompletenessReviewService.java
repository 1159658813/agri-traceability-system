package com.agritrace.backend.service;

import com.agritrace.backend.dao.CompletenessReviewMapper;
import com.agritrace.backend.vo.admin.CompletenessReviewReport;
import com.agritrace.backend.vo.admin.CompletenessReviewRule;
import com.agritrace.backend.vo.admin.CompletenessRuleConfig;
import com.agritrace.backend.vo.farmer.CompletenessFacts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CompletenessReviewService {
    private static final Set<String> KEYS = Set.of(
            "BASE_INFO", "PRODUCT_INFO", "FARM_RECORD", "HARVEST_RECORD", "QUALITY_RECORD", "CIRCULATION_RECORD");
    private static final BigDecimal TOTAL = new BigDecimal("100.00");
    private final CompletenessReviewMapper mapper;
    private final ObjectMapper jsonMapper;

    public CompletenessReviewService(CompletenessReviewMapper mapper, ObjectMapper jsonMapper) {
        this.mapper = mapper;
        this.jsonMapper = jsonMapper;
    }

    public CompletenessReviewReport forAdmin(Long batchId) {
        if (batchId == null || batchId < 1) throw new BatchReviewProblem(400, "批次编号不正确");
        CompletenessFacts facts = mapper.factsForAdmin(batchId);
        if (facts == null) throw new BatchReviewProblem(404, "批次不存在");
        return evaluate(batchId, facts, mapper.enabledRules());
    }

    public CompletenessReviewReport evaluate(Long batchId, CompletenessFacts facts,
                                              List<CompletenessRuleConfig> configuredRules) {
        if (batchId == null || batchId < 1 || facts == null) {
            throw new IllegalArgumentException("batchId and facts are required");
        }
        List<CompletenessRuleConfig> rules = validRules(configuredRules);
        BigDecimal score = BigDecimal.ZERO;
        List<CompletenessReviewRule> details = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (CompletenessRuleConfig rule : rules) {
            boolean filled = switch (rule.getRuleKey()) {
                case "BASE_INFO" -> facts.getBaseCount() > 0;
                case "PRODUCT_INFO" -> facts.getProductCount() > 0;
                case "FARM_RECORD" -> facts.getFarmCount() > 0;
                case "HARVEST_RECORD" -> facts.getHarvestCount() > 0;
                case "QUALITY_RECORD" -> facts.getQualityCount() > 0;
                case "CIRCULATION_RECORD" -> facts.getCirculationCount() > 0;
                default -> false;
            };
            String hint = filled ? null : "请补全：" + rule.getRuleName();
            details.add(new CompletenessReviewRule(rule.getRuleKey(), rule.getRuleName(),
                    rule.getWeight(), rule.isRequired(), filled, hint));
            if (filled) score = score.add(rule.getWeight());
            else missing.add(hint);
        }
        List<String> warnings = new ArrayList<>();
        if (facts.getFailedQualityCount() > 0) warnings.add("存在不通过的质检记录，禁止发布");
        if (facts.getPendingQualityCount() > 0) warnings.add("存在待检测的质检记录，禁止发布");
        if (facts.getPassedQualityCount() == 0) warnings.add("没有有效的 PASS 质检记录，禁止发布");
        warnings.add("评分只反映资料完整性，不代表报告真实或产品质量合格");

        boolean ready = score.compareTo(TOTAL) == 0 && facts.getFailedQualityCount() == 0
                && facts.getPendingQualityCount() == 0 && facts.getPassedQualityCount() > 0;
        return new CompletenessReviewReport(batchId, facts.getBatchStatus(), version(rules),
                score.setScale(2), TOTAL, List.copyOf(details), List.copyOf(missing),
                List.copyOf(warnings), ready);
    }

    public void saveSnapshot(CompletenessReviewReport report) {
        List<Map<String, String>> missing = report.rules().stream()
                .filter(rule -> !rule.filled())
                .map(rule -> Map.of("ruleKey", rule.key(), "message", rule.missingHint()))
                .toList();
        try {
            String json = jsonMapper.writeValueAsString(missing);
            if (mapper.insertSnapshot(report.batchId(), report.ruleVersion(), report.score(), json) != 1) {
                throw new BatchReviewProblem(409, "完整性评分历史保存失败，审核已回滚");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("评分缺失项 JSON 序列化失败", e);
        }
    }

    private List<CompletenessRuleConfig> validRules(List<CompletenessRuleConfig> input) {
        if (input == null || input.size() != KEYS.size()) badRules();
        Set<String> seen = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CompletenessRuleConfig r : input) {
            if (r == null || !KEYS.contains(r.getRuleKey()) || !seen.add(r.getRuleKey())
                    || r.getRuleName() == null || r.getRuleName().isBlank()
                    || r.getWeight() == null || r.getWeight().compareTo(BigDecimal.ZERO) < 0
                    || r.getWeight().compareTo(TOTAL) > 0 || !r.isRequired()) badRules();
            total = total.add(r.getWeight());
        }
        if (total.compareTo(TOTAL) != 0) badRules();
        return input.stream().sorted(Comparator.comparing(CompletenessRuleConfig::getRuleKey)).toList();
    }

    private void badRules() {
        throw new BatchReviewProblem(409, "完整性规则配置异常：必须启用六项必填规则且权重合计100分");
    }

    private String version(List<CompletenessRuleConfig> rules) {
        StringBuilder input = new StringBuilder("completeness-v1;");
        for (CompletenessRuleConfig r : rules) {
            input.append(r.getRuleKey()).append(':').append(r.getWeight().stripTrailingZeros().toPlainString())
                    .append(':').append(r.isRequired()).append(':').append(r.getRuleName()).append(';');
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder("RULES-");
            for (int i = 0; i < 12; i++) hash.append(String.format("%02x", digest[i] & 0xff));
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
