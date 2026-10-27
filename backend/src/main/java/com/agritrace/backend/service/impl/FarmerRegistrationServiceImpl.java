package com.agritrace.backend.service.impl;

import com.agritrace.backend.dao.FarmerRegistrationMapper;
import com.agritrace.backend.dto.farmer.FarmerApplyForm;
import com.agritrace.backend.dto.farmer.NewFarmerAccount;
import com.agritrace.backend.service.FarmerRegistrationService;
import com.agritrace.backend.service.PrivateProofStorage;
import com.agritrace.backend.service.RegistrationProblem;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class FarmerRegistrationServiceImpl implements FarmerRegistrationService {
    private static final Set<String> TYPES = Set.of("BUSINESS_LICENSE", "LAND_CONTRACT", "OTHER_PROOF");
    private final FarmerRegistrationMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final PrivateProofStorage storage;

    public FarmerRegistrationServiceImpl(FarmerRegistrationMapper mapper,
                                         PasswordEncoder passwordEncoder,
                                         PrivateProofStorage storage) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.storage = storage;
    }

    @Override
    @Transactional
    public void apply(FarmerApplyForm form) {
        validate(form);
        form.setUsername(form.getUsername().trim());
        form.setContactName(form.getContactName().trim());
        form.setOrganizationName(form.getOrganizationName() == null
                ? null : form.getOrganizationName().trim());
        form.setContactPhone(form.getContactPhone().trim());
        form.setFarmingLocation(form.getFarmingLocation().trim());
        form.setApplicationNote(form.getApplicationNote() == null
                ? null : form.getApplicationNote().trim());
        if (mapper.countUsername(form.getUsername()) > 0) {
            throw new RegistrationProblem(409, "该账号已被注册，请更换用户名");
        }

        String filePath = storage.store(form.getProofFile());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) storage.remove(filePath);
            }
        });

        try {
            NewFarmerAccount account = new NewFarmerAccount(form.getUsername(),
                    passwordEncoder.encode(form.getPassword()), form.getContactName());
            mapper.createAccount(account);
            mapper.createApplication(account.getId(), form, filePath);
        } catch (DuplicateKeyException ex) {
            throw new RegistrationProblem(409, "该账号已被注册，请更换用户名");
        }
    }

    private void validate(FarmerApplyForm form) {
        if (form == null || form.getUsername() == null
                || !form.getUsername().matches("[a-zA-Z][a-zA-Z0-9_]{3,49}")) {
            throw new RegistrationProblem(400, "账号为4-50位英文、数字或下划线，需以字母开头");
        }
        if (form.getPassword() == null || form.getPassword().length() < 6
                || form.getPassword().length() > 128) {
            throw new RegistrationProblem(400, "密码应为6-128位");
        }
        if (!Set.of("INDIVIDUAL", "COOPERATIVE").contains(form.getApplicantType())) {
            throw new RegistrationProblem(400, "申请主体类型不正确");
        }
        if (form.getContactName() == null || form.getContactName().isBlank()
                || form.getContactName().length() > 80
                || form.getContactPhone() == null || !form.getContactPhone().matches("[0-9+() -]{6,30}")
                || form.getFarmingLocation() == null || form.getFarmingLocation().isBlank()
                || form.getFarmingLocation().length() > 255) {
            throw new RegistrationProblem(400, "请检查联系人、电话和经营地区");
        }
        if ("COOPERATIVE".equals(form.getApplicantType())
                && (form.getOrganizationName() == null || form.getOrganizationName().isBlank())) {
            throw new RegistrationProblem(400, "合作社需填写主体名称");
        }
        if (form.getOrganizationName() != null && form.getOrganizationName().length() > 120
                || form.getApplicationNote() != null && form.getApplicationNote().length() > 500) {
            throw new RegistrationProblem(400, "主体名称或申请说明超出长度限制");
        }
        if (!TYPES.contains(form.getProofType())) {
            throw new RegistrationProblem(400, "请选择正确的资质材料类型");
        }
    }
}
