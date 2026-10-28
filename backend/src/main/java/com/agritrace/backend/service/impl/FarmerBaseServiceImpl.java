package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerBaseMapper;
import com.agritrace.backend.dto.farmer.FarmerBaseForm;
import com.agritrace.backend.service.FarmerBaseProblem;
import com.agritrace.backend.service.FarmerBaseService;
import com.agritrace.backend.vo.farmer.FarmerBasePage;
import com.agritrace.backend.vo.farmer.FarmerBaseRow;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerBaseServiceImpl implements FarmerBaseService {
    private final FarmerBaseMapper mapper;

    public FarmerBaseServiceImpl(FarmerBaseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerBasePage list(Long ownerId, int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 50) {
            throw new FarmerBaseProblem(400, "分页参数不正确");
        }
        long offset = (long) (pageNum - 1) * pageSize;
        return new FarmerBasePage(mapper.count(ownerId), pageNum, pageSize,
                mapper.list(ownerId, pageSize, offset));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerBaseRow find(Long ownerId, Long id) {
        checkId(id);
        FarmerBaseRow row = mapper.find(ownerId, id);
        if (row == null) throw new FarmerBaseProblem(404, "基地不存在或无权访问");
        return row;
    }

    @Override
    @Transactional
    public void create(Long ownerId, FarmerBaseForm form) {
        mapper.create(ownerId, validate(form));
    }

    @Override
    @Transactional
    public void update(Long ownerId, Long id, FarmerBaseForm form) {
        checkId(id);
        FarmerBaseForm clean = validate(form);
        FarmerBaseRow current = find(ownerId, id);
        if (current.getStatus() != 1) {
            throw new FarmerBaseProblem(409, "已停用基地不能编辑，请先恢复使用");
        }
        if (mapper.update(ownerId, id, clean) != 1) {
            throw new FarmerBaseProblem(409, "保存失败，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long ownerId, Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new FarmerBaseProblem(400, "基地状态不正确");
        }
        FarmerBaseRow current = find(ownerId, id);
        if (current.getStatus().equals(status)) return;
        if (status == 0 && mapper.activeBatchCount(id) > 0) {
            throw new FarmerBaseProblem(409, "基地存在草稿或待审核批次，暂不能停用");
        }
        if (mapper.setStatus(ownerId, id, status) != 1) {
            throw new FarmerBaseProblem(409, "状态变更失败，请刷新后重试");
        }
    }

    private void checkId(Long id) {
        if (id == null || id <= 0) throw new FarmerBaseProblem(400, "基地编号不正确");
    }

    private FarmerBaseForm validate(FarmerBaseForm form) {
        if (form == null) throw new FarmerBaseProblem(400, "请填写基地信息");
        String name = required(form.baseName(), 100, "基地名称");
        String province = required(form.province(), 50, "省份");
        String city = required(form.city(), 50, "城市");
        String address = required(form.address(), 255, "详细地址");
        String district = optional(form.district(), 50, "区县");
        String description = optional(form.description(), 500, "基地介绍");
        BigDecimal area = form.areaMu();
        if (area != null && (area.signum() < 0 || area.scale() > 2
                || area.compareTo(new BigDecimal("99999999.99")) > 0)) {
            throw new FarmerBaseProblem(400, "种植面积应为非负数，最多保留两位小数");
        }
        return new FarmerBaseForm(name, province, city, district, address, area, description);
    }

    private String required(String input, int max, String field) {
        String value = optional(input, max, field);
        if (value == null || value.isEmpty()) {
            throw new FarmerBaseProblem(400, "请填写" + field);
        }
        return value;
    }

    private String optional(String input, int max, String field) {
        if (input == null || input.isBlank()) return null;
        String value = input.trim();
        if (value.length() > max) throw new FarmerBaseProblem(400, field + "不能超过" + max + "字");
        return value;
    }
}
