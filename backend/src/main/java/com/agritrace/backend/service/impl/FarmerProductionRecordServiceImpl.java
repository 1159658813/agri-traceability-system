package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerProductionRecordMapper;
import com.agritrace.backend.dto.farmer.FarmWorkForm;
import com.agritrace.backend.dto.farmer.HarvestForm;
import com.agritrace.backend.service.FarmerProductionRecordService;
import com.agritrace.backend.service.FarmerRecordProblem;
import com.agritrace.backend.vo.farmer.BatchRecordInfo;
import com.agritrace.backend.vo.farmer.ProductionRecords;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerProductionRecordServiceImpl implements FarmerProductionRecordService {
    private static final Set<String> OPERATIONS = Set.of(
            "播种", "育苗", "灌溉", "施肥", "病虫害防治", "田间巡查", "其他");
    private final FarmerProductionRecordMapper mapper;

    public FarmerProductionRecordServiceImpl(FarmerProductionRecordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductionRecords list(Long farmerId, Long batchId) {
        BatchRecordInfo batch = find(farmerId, batchId);
        return new ProductionRecords(batch, mapper.listFarm(farmerId, batchId),
                mapper.listHarvest(farmerId, batchId));
    }

    @Override
    @Transactional
    public void createFarm(Long farmerId, Long batchId, FarmWorkForm form) {
        BatchRecordInfo batch = writable(farmerId, batchId);
        FarmWorkForm clean = validateFarm(form, batch);
        if (mapper.createFarm(farmerId, batchId, clean) != 1) {
            throw new FarmerRecordProblem(409, "农事记录保存失败");
        }
    }

    @Override
    @Transactional
    public void updateFarm(Long farmerId, Long batchId, Long recordId, FarmWorkForm form) {
        checkId(recordId);
        BatchRecordInfo batch = writable(farmerId, batchId);
        FarmWorkForm clean = validateFarm(form, batch);
        if (mapper.updateFarm(farmerId, batchId, recordId, clean) != 1) {
            throw new FarmerRecordProblem(404, "农事记录不存在或无权修改");
        }
    }

    @Override
    @Transactional
    public void createHarvest(Long farmerId, Long batchId, HarvestForm form) {
        BatchRecordInfo batch = writable(farmerId, batchId);
        HarvestForm clean = validateHarvest(form, batch);
        if (mapper.createHarvest(batchId, clean) != 1) {
            throw new FarmerRecordProblem(409, "采收记录保存失败");
        }
    }

    @Override
    @Transactional
    public void updateHarvest(Long farmerId, Long batchId, Long recordId, HarvestForm form) {
        checkId(recordId);
        BatchRecordInfo batch = writable(farmerId, batchId);
        HarvestForm clean = validateHarvest(form, batch);
        if (mapper.updateHarvest(farmerId, batchId, recordId, clean) != 1) {
            throw new FarmerRecordProblem(404, "采收记录不存在或无权修改");
        }
    }

    private BatchRecordInfo find(Long farmerId, Long batchId) {
        checkId(batchId);
        BatchRecordInfo batch = mapper.findBatch(farmerId, batchId);
        if (batch == null) throw new FarmerRecordProblem(404, "批次不存在或无权访问");
        return batch;
    }

    private BatchRecordInfo writable(Long farmerId, Long batchId) {
        checkId(batchId);
        // 加锁，防止管理员审核与生产记录修改同时写入。
        BatchRecordInfo batch = mapper.lockBatch(farmerId, batchId);
        if (batch == null) throw new FarmerRecordProblem(404, "批次不存在或无权访问");
        if (!"DRAFT".equals(batch.getStatus()) && !"REJECTED".equals(batch.getStatus())) {
            throw new FarmerRecordProblem(409, "只有草稿或已驳回批次可以修改生产记录");
        }
        return batch;
    }

    private FarmWorkForm validateFarm(FarmWorkForm form, BatchRecordInfo batch) {
        if (form == null || !OPERATIONS.contains(form.operationType())) {
            throw new FarmerRecordProblem(400, "请选择农事作业类型");
        }
        validateDate(form.operatedAt(), batch, "作业时间");
        String description = required(form.description(), 1000, "作业内容");
        String operator = optional(form.operatorName(), 80, "操作人");
        return new FarmWorkForm(form.operationType(), form.operatedAt(), description, operator);
    }

    private HarvestForm validateHarvest(HarvestForm form, BatchRecordInfo batch) {
        if (form == null) throw new FarmerRecordProblem(400, "请填写采收记录");
        validateDate(form.harvestedAt(), batch, "采收时间");
        BigDecimal quantity = form.quantityKg();
        if (quantity == null || quantity.signum() <= 0 || quantity.scale() > 2
                || quantity.compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new FarmerRecordProblem(400, "采收数量必须大于0，最多两位小数");
        }
        String grade = optional(form.grade(), 40, "等级");
        String remark = optional(form.remark(), 500, "采收备注");
        return new HarvestForm(form.harvestedAt(), quantity, grade, remark);
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
        String result = value.trim();
        if (result.length() > max) throw new FarmerRecordProblem(400, field + "最多" + max + "个字符");
        return result;
    }

    private void checkId(Long id) {
        if (id == null || id < 1) throw new FarmerRecordProblem(400, "编号不正确");
    }
}
