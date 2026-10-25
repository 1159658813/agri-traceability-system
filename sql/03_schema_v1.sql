-- 农产品产地溯源系统 V1 数据库脚本
-- MySQL 8.0.36；可在已有 agri_traceability / planting_base 的环境中运行。
-- 只执行 CREATE / ADD，不会 DROP 现有表，也不会删除已有测试数据。
-- 注意：生产环境执行任何 DDL 前仍应备份。
-- 建议保存在项目 sql/03_schema_v1.sql 并提交 Git。

CREATE DATABASE IF NOT EXISTS agri_traceability
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE agri_traceability;

-- 01 后台用户：V1 固定 ADMIN / FARMER 两种角色，消费者无须登录。
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL COMMENT '只保存加盐哈希，禁止存明文密码',
    display_name VARCHAR(80) NOT NULL,
    role_code VARCHAR(20) NOT NULL DEFAULT 'FARMER' COMMENT 'ADMIN/FARMER',
    phone VARCHAR(30) DEFAULT NULL COMMENT '仅后台使用，不对消费者公开',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_role (role_code),
    CONSTRAINT chk_sys_user_role CHECK (role_code IN ('ADMIN', 'FARMER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台用户';

-- 02 种植基地：与此前已建 planting_base 表兼容。
CREATE TABLE IF NOT EXISTS planting_base (
    id BIGINT NOT NULL AUTO_INCREMENT,
    base_name VARCHAR(100) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) DEFAULT NULL,
    address VARCHAR(255) NOT NULL,
    area_mu DECIMAL(10,2) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_base_region (province, city),
    CONSTRAINT chk_base_area CHECK (area_mu IS NULL OR area_mu >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='种植基地';

-- 为现有基地表补充归属用户字段：兼容已插入的“开发测试果园A”。
-- 历史测试记录 owner_id 允许暂时为 NULL，后续建真实农户账户后再补齐。
SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'planting_base'
       AND COLUMN_NAME = 'owner_id') = 0,
    'ALTER TABLE planting_base ADD COLUMN owner_id BIGINT NULL AFTER id',
    'SELECT 1'
);
PREPARE p FROM @ddl; EXECUTE p; DEALLOCATE PREPARE p;
SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'planting_base'
       AND INDEX_NAME = 'idx_planting_base_owner') = 0,
    'ALTER TABLE planting_base ADD INDEX idx_planting_base_owner (owner_id)',
    'SELECT 1'
);
PREPARE p FROM @ddl; EXECUTE p; DEALLOCATE PREPARE p;
SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
     WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'planting_base'
       AND CONSTRAINT_NAME = 'fk_planting_base_owner') = 0,
    'ALTER TABLE planting_base ADD CONSTRAINT fk_planting_base_owner FOREIGN KEY (owner_id) REFERENCES sys_user(id)',
    'SELECT 1'
);
PREPARE p FROM @ddl; EXECUTE p; DEALLOCATE PREPARE p;

-- 03 产品分类
CREATE TABLE IF NOT EXISTS product_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(60) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农产品分类';

-- 04 农产品档案：产品与种植基地不直接绑定，二者由具体批次关联。
CREATE TABLE IF NOT EXISTS agri_product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    variety VARCHAR(100) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    cover_image_url VARCHAR(500) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_agri_product_category (category_id),
    KEY idx_agri_product_owner (owner_id),
    CONSTRAINT fk_agri_product_category FOREIGN KEY (category_id) REFERENCES product_category(id),
    CONSTRAINT fk_agri_product_owner FOREIGN KEY (owner_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农产品档案';

-- 05 溯源批次：V1 不支持跨基地混批、批次拆分合并。
CREATE TABLE IF NOT EXISTS trace_batch (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_no VARCHAR(60) NOT NULL COMMENT '内部批次编号',
    trace_code VARCHAR(80) NOT NULL COMMENT '公开查询码，由服务端生成，不直接使用主键',
    product_id BIGINT NOT NULL,
    base_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    planting_date DATE DEFAULT NULL,
    planned_harvest_date DATE DEFAULT NULL,
    estimated_quantity_kg DECIMAL(12,2) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/SUBMITTED/PUBLISHED/REJECTED',
    remark VARCHAR(500) DEFAULT NULL,
    published_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_trace_batch_no (batch_no),
    UNIQUE KEY uk_trace_batch_code (trace_code),
    KEY idx_trace_batch_product (product_id),
    KEY idx_trace_batch_base (base_id),
    KEY idx_trace_batch_owner_status (owner_id, status),
    CONSTRAINT fk_trace_batch_product FOREIGN KEY (product_id) REFERENCES agri_product(id),
    CONSTRAINT fk_trace_batch_base FOREIGN KEY (base_id) REFERENCES planting_base(id),
    CONSTRAINT fk_trace_batch_owner FOREIGN KEY (owner_id) REFERENCES sys_user(id),
    CONSTRAINT chk_trace_batch_status CHECK (status IN ('DRAFT','SUBMITTED','PUBLISHED','REJECTED')),
    CONSTRAINT chk_trace_batch_quantity CHECK (estimated_quantity_kg IS NULL OR estimated_quantity_kg >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源批次';

-- 06 农事记录
CREATE TABLE IF NOT EXISTS farm_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    operation_type VARCHAR(40) NOT NULL COMMENT '施肥/灌溉/病虫害防治等，由业务字典约束',
    operated_at DATETIME NOT NULL,
    description VARCHAR(1000) NOT NULL,
    operator_name VARCHAR(80) DEFAULT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_farm_record_batch_time (batch_id, operated_at),
    CONSTRAINT fk_farm_record_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id),
    CONSTRAINT fk_farm_record_user FOREIGN KEY (created_by) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='种植与农事记录';

-- 07 采收记录：允许同一批次分多次采收。
CREATE TABLE IF NOT EXISTS harvest_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    harvested_at DATETIME NOT NULL,
    quantity_kg DECIMAL(12,2) NOT NULL,
    grade VARCHAR(40) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_harvest_record_batch_time (batch_id, harvested_at),
    CONSTRAINT fk_harvest_record_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id),
    CONSTRAINT chk_harvest_quantity CHECK (quantity_kg > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采收记录';

-- 08 质检记录：结论需要真实报告支撑；测试数据须明确标注。
CREATE TABLE IF NOT EXISTS quality_inspection (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    inspected_at DATETIME NOT NULL,
    inspection_item VARCHAR(150) NOT NULL,
    inspection_org VARCHAR(150) DEFAULT NULL,
    report_no VARCHAR(100) DEFAULT NULL,
    conclusion VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PASS/FAIL',
    remark VARCHAR(500) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_quality_inspection_batch (batch_id, inspected_at),
    CONSTRAINT fk_quality_inspection_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id),
    CONSTRAINT chk_quality_inspection_result CHECK (conclusion IN ('PENDING','PASS','FAIL'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次质检记录';

-- 09 流通记录：按时间线记录打包、出库、运输和签收等事件。
CREATE TABLE IF NOT EXISTS circulation_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    event_at DATETIME NOT NULL,
    from_place VARCHAR(150) DEFAULT NULL,
    to_place VARCHAR(150) DEFAULT NULL,
    operator_name VARCHAR(80) DEFAULT NULL,
    logistics_no VARCHAR(100) DEFAULT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_circulation_record_batch_time (batch_id, event_at),
    CONSTRAINT fk_circulation_record_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流通事件记录';

-- 10 资料附件：元数据在数据库，实际图片/PDF文件存文件系统或对象存储。
CREATE TABLE IF NOT EXISTS trace_attachment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    related_type VARCHAR(40) NOT NULL COMMENT 'BATCH/FARM/HARVEST/QUALITY/CIRCULATION',
    related_id BIGINT DEFAULT NULL COMMENT '关联对应记录ID，按类型在业务层核验',
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(40) NOT NULL,
    public_visible TINYINT NOT NULL DEFAULT 0,
    uploaded_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_trace_attachment_batch (batch_id),
    KEY idx_trace_attachment_relation (related_type, related_id),
    CONSTRAINT fk_trace_attachment_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id),
    CONSTRAINT fk_trace_attachment_user FOREIGN KEY (uploaded_by) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源资料附件';

-- 11 产地与品种特色：仅对选定的基地-产品组合维护描述。
CREATE TABLE IF NOT EXISTS origin_profile (
    id BIGINT NOT NULL AUTO_INCREMENT,
    base_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    climate_info VARCHAR(500) DEFAULT NULL,
    soil_info VARCHAR(500) DEFAULT NULL,
    flavor_note VARCHAR(500) DEFAULT NULL COMMENT '一般性风味介绍，非具体批次实测结果',
    feature_tags VARCHAR(255) DEFAULT NULL COMMENT 'V1以逗号分隔简化管理',
    information_source VARCHAR(500) DEFAULT NULL COMMENT '介绍信息来源',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_origin_profile_combo (base_id, product_id),
    CONSTRAINT fk_origin_profile_base FOREIGN KEY (base_id) REFERENCES planting_base(id),
    CONSTRAINT fk_origin_profile_product FOREIGN KEY (product_id) REFERENCES agri_product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产地与品种特色';

-- 12 批次审核日志：不可把未审核草稿公开为真实溯源记录。
CREATE TABLE IF NOT EXISTS batch_audit (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    decision VARCHAR(20) NOT NULL COMMENT 'APPROVE/REJECT',
    comment VARCHAR(500) DEFAULT NULL,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_batch_audit_batch_time (batch_id, reviewed_at),
    CONSTRAINT fk_batch_audit_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id),
    CONSTRAINT fk_batch_audit_reviewer FOREIGN KEY (reviewer_id) REFERENCES sys_user(id),
    CONSTRAINT chk_batch_audit_decision CHECK (decision IN ('APPROVE','REJECT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次审核记录';

-- 13 完整性规则：权重仅用于资料完整度，不代表产品质量或信息真实性。
CREATE TABLE IF NOT EXISTS completeness_rule (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rule_key VARCHAR(80) NOT NULL,
    stage_code VARCHAR(40) NOT NULL,
    rule_name VARCHAR(120) NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    required_flag TINYINT NOT NULL DEFAULT 1,
    enabled TINYINT NOT NULL DEFAULT 1,
    rule_description VARCHAR(500) DEFAULT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_completeness_rule_key (rule_key),
    CONSTRAINT chk_completeness_rule_weight CHECK (weight >= 0 AND weight <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源信息完整性规则';

-- 14 完整性评分历史：记录评价快照便于论文测试和审计。
CREATE TABLE IF NOT EXISTS completeness_result (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    rule_version VARCHAR(40) NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    missing_items JSON NOT NULL COMMENT '缺失项目代码和提示信息',
    evaluated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_completeness_result_batch_time (batch_id, evaluated_at),
    CONSTRAINT fk_completeness_result_batch FOREIGN KEY (batch_id) REFERENCES trace_batch(id),
    CONSTRAINT chk_completeness_score CHECK (score >= 0 AND score <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='完整性评价记录';

-- 示例规则：总权重100；业务服务层依据rule_key实施实际校验。
-- ON DUPLICATE KEY UPDATE 不覆盖已经人工调整过的权重，仅补充缺失规则。
INSERT INTO completeness_rule (rule_key, stage_code, rule_name, weight, rule_description)
VALUES
('BASE_INFO', 'BASE', '关联有效种植基地', 15, '批次已关联有效基地且地点信息完整'),
('PRODUCT_INFO', 'BASE', '关联有效农产品', 10, '批次已关联农产品且品种资料完整'),
('FARM_RECORD', 'FARM', '农事记录', 20, '至少有一条包含日期和内容的农事记录'),
('HARVEST_RECORD', 'HARVEST', '采收记录', 20, '至少一条有效采收日期及数量记录'),
('QUALITY_RECORD', 'QUALITY', '质检记录', 20, '质检项目与结论记录完整；不代表检测合格'),
('CIRCULATION_RECORD', 'CIRCULATION', '流通记录', 15, '至少一条有效流通事件记录')
ON DUPLICATE KEY UPDATE rule_key = VALUES(rule_key);

-- 本脚本不创建默认账号：请在后续账户模块使用安全密码哈希建立测试用户。
-- 旧基地 owner_id 目前可以为空；开发农户权限时必须补齐并严格验证归属。
-- 查询确认：
-- SHOW TABLES;
-- SELECT COUNT(*) AS table_count FROM information_schema.TABLES
-- WHERE TABLE_SCHEMA = 'agri_traceability' AND TABLE_TYPE = 'BASE TABLE';
-- SELECT id, base_name, owner_id FROM planting_base;
