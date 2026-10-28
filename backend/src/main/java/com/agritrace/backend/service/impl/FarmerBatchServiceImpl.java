package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerBatchMapper;
import com.agritrace.backend.dto.farmer.FarmerBatchForm;
import com.agritrace.backend.service.FarmerBatchProblem;
import com.agritrace.backend.service.FarmerBatchService;
import com.agritrace.backend.vo.farmer.FarmerBatchOptions;
import com.agritrace.backend.vo.farmer.FarmerBatchPage;
import com.agritrace.backend.vo.farmer.FarmerBatchRow;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerBatchServiceImpl implements FarmerBatchService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final FarmerBatchMapper mapper;

    public FarmerBatchServiceImpl(FarmerBatchMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerBatchOptions options(Long farmerId) {
        return new FarmerBatchOptions(mapper.products(farmerId), mapper.bases(farmerId));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerBatchPage list(Long farmerId, int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 50) {
            throw new FarmerBatchProblem(400, "分页参数不正确");
        }
        long offset = ((long) pageNum - 1) * pageSize;
        return new FarmerBatchPage(mapper.count(farmerId), pageNum, pageSize,
                mapper.list(farmerId, pageSize, offset));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerBatchRow find(Long farmerId, Long id) {
        if (id == null || id < 1) throw new FarmerBatchProblem(400, "批次编号不正确");
        FarmerBatchRow row = mapper.find(farmerId, id);
        if (row == null) throw new FarmerBatchProblem(404, "批次不存在或无权访问");
        return row;
    }

    @Override
    @Transactional
    public void create(Long farmerId, FarmerBatchForm form) {
        FarmerBatchForm clean = validate(farmerId, form);
        String batchNo = "BT" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + token(6);
        String traceCode = "TR" + token(12);
        if (mapper.create(farmerId, batchNo, traceCode, clean) != 1) {
            throw new FarmerBatchProblem(409, "创建批次失败");
        }
    }

    @Override
    @Transactional
    public void update(Long farmerId, Long id, FarmerBatchForm form) {
        FarmerBatchRow old = find(farmerId, id);
        if (!"DRAFT".equals(old.getStatus()) && !"REJECTED".equals(old.getStatus())) {
            throw new FarmerBatchProblem(409, "仅草稿或已驳回批次允许编辑");
        }
        FarmerBatchForm clean = validate(farmerId, form);
        if (mapper.update(farmerId, id, clean) != 1) {
            throw new FarmerBatchProblem(409, "批次状态已变化，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void submit(Long farmerId, Long id) {
        FarmerBatchRow row = find(farmerId, id);
        if (!"DRAFT".equals(row.getStatus())) {
            throw new FarmerBatchProblem(409, "仅草稿批次允许提交，请先编辑已驳回批次");
        }
        if (row.getPlantingDate() == null) {
            throw new FarmerBatchProblem(400, "提交审核前请填写种植日期");
        }
        if (mapper.submit(farmerId, id) != 1) {
            throw new FarmerBatchProblem(409, "产品或基地已停用，或批次状态已变化，请刷新后重试");
        }
    }

    private FarmerBatchForm validate(Long farmerId, FarmerBatchForm form) {
        if (form == null) throw new FarmerBatchProblem(400, "请填写批次信息");
        if (form.productId() == null || form.productId() < 1
                || mapper.validProduct(farmerId, form.productId()) != 1) {
            throw new FarmerBatchProblem(400, "请选择自己名下正在使用的产品");
        }
        if (form.baseId() == null || form.baseId() < 1
                || mapper.validBase(farmerId, form.baseId()) != 1) {
            throw new FarmerBatchProblem(400, "请选择自己名下正在使用的基地");
        }
        LocalDate planting = form.plantingDate();
        LocalDate harvest = form.plannedHarvestDate();
        if (planting != null && harvest != null && harvest.isBefore(planting)) {
            throw new FarmerBatchProblem(400, "预计采收日期不能早于种植日期");
        }
        BigDecimal quantity = form.estimatedQuantityKg();
        if (quantity != null && (quantity.signum() < 0 || quantity.scale() > 2
                || quantity.compareTo(new BigDecimal("9999999999.99")) > 0)) {
            throw new FarmerBatchProblem(400, "预计产量不能为负数，最多保留两位小数");
        }
        String remark = form.remark() == null ? null : form.remark().trim();
        if (remark != null && remark.length() > 500) {
            throw new FarmerBatchProblem(400, "批次备注不能超过500字");
        }
        if (remark != null && remark.isEmpty()) remark = null;
        return new FarmerBatchForm(form.productId(), form.baseId(), planting, harvest, quantity, remark);
    }

    private String token(int byteCount) {
        byte[] bytes = new byte[byteCount];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }
}
