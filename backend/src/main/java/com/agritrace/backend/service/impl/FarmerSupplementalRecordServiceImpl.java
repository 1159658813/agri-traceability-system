package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerProductionRecordMapper;
import com.agritrace.backend.dao.FarmerSupplementalRecordMapper;
import com.agritrace.backend.dto.farmer.CirculationForm;
import com.agritrace.backend.dto.farmer.QualityInspectionForm;
import com.agritrace.backend.service.FarmerRecordProblem;
import com.agritrace.backend.service.FarmerSupplementalRecordService;
import com.agritrace.backend.vo.farmer.BatchRecordInfo;
import com.agritrace.backend.vo.farmer.SupplementalRecords;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerSupplementalRecordServiceImpl implements FarmerSupplementalRecordService {
    private static final Set<String> CONCLUSIONS = Set.of("PENDING", "PASS", "FAIL");
    private static final Set<String> EVENTS = Set.of("包装", "出库", "运输", "到达", "签收", "其他");
    private final FarmerProductionRecordMapper batchMapper;
    private final FarmerSupplementalRecordMapper mapper;

    public FarmerSupplementalRecordServiceImpl(FarmerProductionRecordMapper batchMapper,
                                               FarmerSupplementalRecordMapper mapper) {
        this.batchMapper = batchMapper;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SupplementalRecords list(Long farmerId, Long batchId) {
        checkId(batchId);
        if (batchMapper.findBatch(farmerId, batchId) == null) {
            throw new FarmerRecordProblem(404, "批次不存在或无权访问");
        }
        return new SupplementalRecords(mapper.listQuality(farmerId, batchId),
                mapper.listCirculation(farmerId, batchId));
    }

    @Override
    @Transactional
    public void createQuality(Long farmerId, Long batchId, QualityInspectionForm form) {
        BatchRecordInfo batch = writable(farmerId, batchId);
        QualityInspectionForm clean = validateQuality(form, batch);
        if (mapper.createQuality(batchId, clean) != 1) {
            throw new FarmerRecordProblem(409, "质检记录保存失败");
        }
    }

    @Override
    @Transactional
    public void updateQuality(Long farmerId, Long batchId, Long recordId, QualityInspectionForm form) {
        checkId(recordId);
        BatchRecordInfo batch = writable(farmerId, batchId);
        QualityInspectionForm clean = validateQuality(form, batch);
        if (mapper.updateQuality(farmerId, batchId, recordId, clean) != 1) {
            throw new FarmerRecordProblem(404, "质检记录不存在或无权修改");
        }
    }

    @Override
    @Transactional
    public void createCirculation(Long farmerId, Long batchId, CirculationForm form) {
        BatchRecordInfo batch = writable(farmerId, batchId);
        CirculationForm clean = validateCirculation(form, batch);
        if (mapper.createCirculation(batchId, clean) != 1) {
            throw new FarmerRecordProblem(409, "流通事件保存失败");
        }
    }

    @Override
    @Transactional
    public void updateCirculation(Long farmerId, Long batchId, Long recordId, CirculationForm form) {
        checkId(recordId);
        BatchRecordInfo batch = writable(farmerId, batchId);
        CirculationForm clean = validateCirculation(form, batch);
        if (mapper.updateCirculation(farmerId, batchId, recordId, clean) != 1) {
            throw new FarmerRecordProblem(404, "流通事件不存在或无权修改");
        }
    }

    private BatchRecordInfo writable(Long farmerId, Long batchId) {
        checkId(batchId);
        BatchRecordInfo batch = batchMapper.lockBatch(farmerId, batchId);
        if (batch == null) throw new FarmerRecordProblem(404, "批次不存在或无权访问");
        if (!"DRAFT".equals(batch.getStatus()) && !"REJECTED".equals(batch.getStatus())) {
            throw new FarmerRecordProblem(409, "只有草稿或已驳回批次可以修改记录");
        }
        return batch;
    }

    private QualityInspectionForm validateQuality(QualityInspectionForm form, BatchRecordInfo batch) {
        if (form == null) throw new FarmerRecordProblem(400, "请填写质检记录");
        validateDate(form.inspectedAt(), batch, "检测时间");
        String item = required(form.inspectionItem(), 150, "检测项目");
        String org = optional(form.inspectionOrg(), 150, "检测机构");
        String reportNo = optional(form.reportNo(), 100, "报告编号");
        String conclusion = form.conclusion() == null ? "PENDING" : form.conclusion().trim();
        if (!CONCLUSIONS.contains(conclusion)) throw new FarmerRecordProblem(400, "检测结论不正确");
        String remark = optional(form.remark(), 500, "检测备注");
        if (!"PENDING".equals(conclusion) && (remark == null || !remark.contains("【演示】"))) {
            throw new FarmerRecordProblem(400, "正式检测报告附件功能尚未开放，PASS/FAIL 仅允许标注【演示】的测试记录");
        }
        return new QualityInspectionForm(form.inspectedAt(), item, org, reportNo, conclusion, remark);
    }

    private CirculationForm validateCirculation(CirculationForm form, BatchRecordInfo batch) {
        if (form == null || form.eventType() == null || !EVENTS.contains(form.eventType())) {
            throw new FarmerRecordProblem(400, "请选择流通事件类型");
        }
        validateDate(form.eventAt(), batch, "事件时间");
        return new CirculationForm(form.eventType(), form.eventAt(),
                optional(form.fromPlace(), 150, "始发地"),
                optional(form.toPlace(), 150, "到达地"),
                optional(form.operatorName(), 80, "经办人"),
                optional(form.logisticsNo(), 100, "物流单号"),
                optional(form.remark(), 500, "事件备注"));
    }

    private void validateDate(LocalDateTime value, BatchRecordInfo batch, String field) {
        if (value == null || value.isAfter(LocalDateTime.now())) {
            throw new FarmerRecordProblem(400, field + "不能为空或晚于当前时间");
        }
        if (batch.getPlantingDate() != null && value.toLocalDate().isBefore(batch.getPlantingDate())) {
            throw new FarmerRecordProblem(400, field + "不能早于批次种植日期");
        }
    }

    private String required(String value, int max, String field) {
        String text = optional(value, max, field);
        if (text == null) throw new FarmerRecordProblem(400, "请填写" + field);
        return text;
    }

    private String optional(String value, int max, String field) {
        if (value == null || value.isBlank()) return null;
        String text = value.trim();
        if (text.length() > max) throw new FarmerRecordProblem(400, field + "最多" + max + "个字符");
        return text;
    }

    private void checkId(Long value) {
        if (value == null || value < 1) throw new FarmerRecordProblem(400, "编号不正确");
    }
}
