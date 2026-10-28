package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerProductMapper;
import com.agritrace.backend.dto.farmer.FarmerProductForm;
import com.agritrace.backend.service.FarmerProductProblem;
import com.agritrace.backend.service.FarmerProductService;
import com.agritrace.backend.vo.farmer.FarmerProductCategory;
import com.agritrace.backend.vo.farmer.FarmerProductPage;
import com.agritrace.backend.vo.farmer.FarmerProductRow;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmerProductServiceImpl implements FarmerProductService {
    private final FarmerProductMapper mapper;

    public FarmerProductServiceImpl(FarmerProductMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmerProductCategory> categories() {
        return mapper.categories();
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerProductPage list(Long farmerId, int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 50) {
            throw new FarmerProductProblem(400, "分页参数不正确");
        }
        long offset = ((long) pageNum - 1) * pageSize;
        return new FarmerProductPage(mapper.count(farmerId), pageNum, pageSize,
                mapper.list(farmerId, pageSize, offset));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmerProductRow find(Long farmerId, Long id) {
        checkId(id);
        FarmerProductRow row = mapper.find(farmerId, id);
        if (row == null) throw new FarmerProductProblem(404, "产品不存在或无权访问");
        return row;
    }

    @Override
    @Transactional
    public void create(Long farmerId, FarmerProductForm form) {
        mapper.create(farmerId, validate(form));
    }

    @Override
    @Transactional
    public void update(Long farmerId, Long id, FarmerProductForm form) {
        checkId(id);
        FarmerProductForm clean = validate(form);
        FarmerProductRow current = find(farmerId, id);
        if (current.getStatus() != 1) {
            throw new FarmerProductProblem(409, "已停用的产品不能编辑，请先恢复使用");
        }
        if (mapper.update(farmerId, id, clean) != 1) {
            throw new FarmerProductProblem(409, "保存失败，请刷新后重试");
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long farmerId, Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new FarmerProductProblem(400, "产品状态不正确");
        }
        FarmerProductRow current = find(farmerId, id);
        if (current.getStatus().equals(status)) return;
        if (status == 0 && mapper.activeBatchCount(farmerId, id) > 0) {
            throw new FarmerProductProblem(409, "产品关联草稿或待审核批次，暂不能停用");
        }
        if (status == 1 && !Integer.valueOf(1).equals(mapper.categoryStatus(current.getCategoryId()))) {
            throw new FarmerProductProblem(409, "所属分类已停用，请先联系管理员");
        }
        if (mapper.setStatus(farmerId, id, status) != 1) {
            throw new FarmerProductProblem(409, "状态变更失败，请刷新后重试");
        }
    }

    private FarmerProductForm validate(FarmerProductForm form) {
        if (form == null) throw new FarmerProductProblem(400, "请填写产品信息");
        Long categoryId = form.categoryId();
        if (categoryId == null || categoryId < 1
                || !Integer.valueOf(1).equals(mapper.categoryStatus(categoryId))) {
            throw new FarmerProductProblem(400, "请选择有效的产品分类");
        }
        String name = required(form.productName(), 100, "产品名称");
        String variety = optional(form.variety(), 100, "品种");
        String description = optional(form.description(), 2000, "产品介绍");
        return new FarmerProductForm(categoryId, name, variety, description);
    }

    private void checkId(Long id) {
        if (id == null || id <= 0) throw new FarmerProductProblem(400, "产品编号不正确");
    }

    private String required(String value, int max, String field) {
        String result = optional(value, max, field);
        if (result == null) throw new FarmerProductProblem(400, "请填写" + field);
        return result;
    }

    private String optional(String value, int max, String field) {
        if (value == null || value.isBlank()) return null;
        String result = value.trim();
        if (result.length() > max) throw new FarmerProductProblem(400, field + "不能超过" + max + "字");
        return result;
    }
}
