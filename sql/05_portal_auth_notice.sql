-- 农产品产地溯源系统 V1.2 增量迁移脚本
-- 依赖现有 14 张 V1 表，其中 sys_user.id 是管理员/农户主键。
-- 本脚本只创建两张新表，不删除、不清空现有数据。
-- 执行前备份数据库。适配 MySQL 8.0.36。

USE agri_traceability;

-- 农户注册资质申请。申请文件必须存储在不可直接公开访问的私有目录。
-- 一个账号可保留多次申请历史，业务层限制同一账号同时只有一条 PENDING 申请。
CREATE TABLE IF NOT EXISTS farmer_application (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    applicant_id BIGINT NOT NULL COMMENT '申请账号ID，关联sys_user',
    applicant_type VARCHAR(20) NOT NULL COMMENT 'INDIVIDUAL或COOPERATIVE',
    organization_name VARCHAR(120) DEFAULT NULL COMMENT '合作社/经营主体名称（个人农户可为空）',
    contact_name VARCHAR(80) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(30) NOT NULL COMMENT '联系电话',
    farming_location VARCHAR(255) NOT NULL COMMENT '生产经营地区（描述）',
    proof_type VARCHAR(40) NOT NULL COMMENT '资质材料类型',
    proof_file_path VARCHAR(500) NOT NULL COMMENT '私有文件存储路径，不可直接作为公开URL',
    application_note VARCHAR(500) DEFAULT NULL COMMENT '申请说明',
    review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    reviewer_id BIGINT DEFAULT NULL COMMENT '审核管理员ID',
    review_comment VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    reviewed_at DATETIME DEFAULT NULL COMMENT '审核时间',
    PRIMARY KEY (id),
    KEY idx_farm_apply_user_time (applicant_id, submitted_at),
    KEY idx_farm_apply_status_time (review_status, submitted_at),
    CONSTRAINT fk_farm_apply_user FOREIGN KEY (applicant_id) REFERENCES sys_user(id),
    CONSTRAINT fk_farm_apply_reviewer FOREIGN KEY (reviewer_id) REFERENCES sys_user(id),
    CONSTRAINT chk_farm_apply_type CHECK (applicant_type IN ('INDIVIDUAL','COOPERATIVE')),
    CONSTRAINT chk_farm_apply_status CHECK (review_status IN ('PENDING','APPROVED','REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农户资质审核申请';

-- 平台公告。游客只能查询已发布、已到发布时间且尚未过期的公告。
-- 后台使用纯文本或经过安全处理的内容，避免直接渲染未过滤的HTML。
CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(150) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告正文',
    notice_type VARCHAR(20) NOT NULL DEFAULT 'GENERAL' COMMENT 'GENERAL/POLICY/MAINTENANCE',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/WITHDRAWN',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶',
    published_at DATETIME DEFAULT NULL COMMENT '首次公开发布时间',
    expires_at DATETIME DEFAULT NULL COMMENT '过期时间；空表示不设过期时间',
    created_by BIGINT NOT NULL COMMENT '创建管理员ID',
    updated_by BIGINT DEFAULT NULL COMMENT '最后修改管理员ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notice_public (status, is_top, published_at),
    CONSTRAINT fk_notice_creator FOREIGN KEY (created_by) REFERENCES sys_user(id),
    CONSTRAINT fk_notice_updater FOREIGN KEY (updated_by) REFERENCES sys_user(id),
    CONSTRAINT chk_notice_type CHECK (notice_type IN ('GENERAL','POLICY','MAINTENANCE')),
    CONSTRAINT chk_notice_status CHECK (status IN ('DRAFT','PUBLISHED','WITHDRAWN')),
    CONSTRAINT chk_notice_top CHECK (is_top IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台公告';

-- 验证
SHOW TABLES LIKE 'farmer_application';
SHOW TABLES LIKE 'sys_notice';

-- 注意：本版不修改 sys_user 的 status 定义。
-- 注册 Service 必须显式创建 role_code='FARMER', status=0 的待审核账号。
-- 管理员批准时在同一事务内更新申请为 APPROVED、设置 sys_user.status=1。
-- 管理员账号仅允许管理员初始化，公共注册接口不能传入或修改 role_code。
