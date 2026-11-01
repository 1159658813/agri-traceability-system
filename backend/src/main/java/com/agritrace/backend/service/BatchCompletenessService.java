package com.agritrace.backend.service;

import com.agritrace.backend.dao.BatchCompletenessMapper;
import com.agritrace.backend.vo.farmer.BatchCompletenessReport;
import com.agritrace.backend.vo.farmer.CompletenessFacts;
import com.agritrace.backend.vo.farmer.CompletenessItem;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchCompletenessService {
    private final BatchCompletenessMapper mapper;

    public BatchCompletenessService(BatchCompletenessMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public BatchCompletenessReport forFarmer(Long farmerId, Long batchId) {
        if (farmerId == null || farmerId < 1 || batchId == null || batchId < 1) {
            throw new FarmerRecordProblem(400, "账号或批次编号不正确");
        }
        CompletenessFacts facts = mapper.selectFacts(farmerId, batchId);
        if (facts == null) {
            throw new FarmerRecordProblem(404, "批次不存在或无权访问");
        }
        return evaluate(facts);
    }

    public BatchCompletenessReport evaluate(CompletenessFacts facts) {
        if (facts == null) throw new IllegalArgumentException("facts cannot be null");
        List<CompletenessItem> items = List.of(
                item("BASE", "基地关联", 15, facts.getBaseCount() > 0, "请关联本人种植基地"),
                item("PRODUCT", "产品关联", 10, facts.getProductCount() > 0, "请关联本人农产品档案"),
                item("FARM", "农事记录", 20, facts.getFarmCount() > 0, "请至少填写一条农事记录"),
                item("HARVEST", "采收记录", 20, facts.getHarvestCount() > 0, "请至少填写一条采收记录"),
                item("QUALITY", "质检记录", 20, facts.getQualityCount() > 0, "请至少登记一条有检测项目和时间的记录"),
                item("CIRCULATION", "流通记录", 15, facts.getCirculationCount() > 0, "请至少登记一条有事件类型和时间的流通记录")
        );
        int score = items.stream().filter(CompletenessItem::filled)
                .mapToInt(CompletenessItem::weight).sum();
        List<String> missing = items.stream().filter(item -> !item.filled())
                .map(CompletenessItem::missingHint).toList();

        String qualityNotice;
        if (facts.getFailedQualityCount() > 0) {
            qualityNotice = "存在结论为不通过的质检记录；正式发布必须经过额外审核拦截";
        } else if (facts.getPendingQualityCount() > 0) {
            qualityNotice = "存在待检测记录，评分不表示检测已通过";
        } else {
            qualityNotice = "本评分不核验质检报告真实性，也不代表检测合格";
        }
        return new BatchCompletenessReport(facts.getBatchStatus(), score, 100, items,
                missing, qualityNotice,
                "当前为资料存在性预览，按六项默认权重计算；尚未读取数据库规则快照、核验附件或决定发布资格。");
    }

    private CompletenessItem item(String code, String label, int weight, boolean filled, String hint) {
        return new CompletenessItem(code, label, weight, filled, filled ? null : hint);
    }
}
