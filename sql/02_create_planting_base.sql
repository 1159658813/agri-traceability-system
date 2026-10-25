USE agri_traceability;

CREATE TABLE IF NOT EXISTS planting_base (
                                             id BIGINT NOT NULL AUTO_INCREMENT COMMENT '基地ID',
                                             base_name VARCHAR(100) NOT NULL COMMENT '基地名称',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) DEFAULT NULL COMMENT '区县',
    address VARCHAR(255) NOT NULL COMMENT '详细地址',
    area_mu DECIMAL(10,2) DEFAULT NULL COMMENT '种植面积，单位亩',
    description VARCHAR(500) DEFAULT NULL COMMENT '基地介绍',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    INDEX idx_base_region (province, city),
    CONSTRAINT chk_base_area CHECK (area_mu IS NULL OR area_mu >= 0)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COMMENT='种植基地信息表';