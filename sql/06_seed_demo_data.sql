-- 田野档案 V1.3 / local DEMO data (MySQL 8.x)
-- SQL source: V1 03_schema_v1 + V1.2 05_portal_auth_notice definitions.
-- Only for local development, QA and graduation defense. NOT real farms / inspection / publication.
-- Existing records are preserved; rerunning adds no duplicated DEMO rows.
-- Run only after backing up agri_traceability. Do NOT execute against production.
-- Script uses DELIMITER and must be sourced from the mysql CLI, not a JDBC SQL editor.
SET NAMES utf8mb4;
USE agri_traceability;

DELIMITER $$
DROP PROCEDURE IF EXISTS seed_tyd_demo_v13$$
CREATE PROCEDURE seed_tyd_demo_v13()
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  -- Mock identities have unshared random BCrypt passwords; they are NOT usable demo login accounts.
  INSERT INTO sys_user (username, password_hash, display_name, role_code, status, created_at)
  SELECT 'demo_seed_admin', '$2y$10$gKtRF2KsgDsU/HQOKh0DguSI/Vnj9P2MswElDXlDp4BBUNI/P6o3.', '演示平台管理员', 'ADMIN', 1, DATE_SUB(NOW(), INTERVAL 9 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo_seed_admin');

  -- Mock identities have unshared random BCrypt passwords; they are NOT usable demo login accounts.
  INSERT INTO sys_user (username, password_hash, display_name, role_code, status, created_at)
  SELECT 'demo_seed_farmer_sc', '$2y$10$Ul8.AKRPfrEiHCJg1rTid.2AE4sJRiunDZ9DJDeWpZWcb6nKrTrqS', '演示农户·四川', 'FARMER', 1, DATE_SUB(NOW(), INTERVAL 9 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo_seed_farmer_sc');

  -- Mock identities have unshared random BCrypt passwords; they are NOT usable demo login accounts.
  INSERT INTO sys_user (username, password_hash, display_name, role_code, status, created_at)
  SELECT 'demo_seed_farmer_sd', '$2y$10$k4QmVoRZgL9wAaFyuf.0Qe7nJ5FecFDQ/Fs8WXVidvjTSZf5Ho18u', '演示农户·山东', 'FARMER', 1, DATE_SUB(NOW(), INTERVAL 9 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo_seed_farmer_sd');

  -- Mock identities have unshared random BCrypt passwords; they are NOT usable demo login accounts.
  INSERT INTO sys_user (username, password_hash, display_name, role_code, status, created_at)
  SELECT 'demo_seed_farmer_zj', '$2y$10$X6Lm3sd9CKqFexQRKAshluNGPhnWSRDgkSqvyHblXQu/0vD28nWn.', '演示农户·浙江', 'FARMER', 1, DATE_SUB(NOW(), INTERVAL 9 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo_seed_farmer_zj');

  -- Mock identities have unshared random BCrypt passwords; they are NOT usable demo login accounts.
  INSERT INTO sys_user (username, password_hash, display_name, role_code, status, created_at)
  SELECT 'demo_seed_farmer_pending', '$2y$10$2H5fqGGMY6IXeSWqxg8BIeOHWBIJbGMhH6HlyBp/XCQYRjBALPMDi', '演示农户·待审核', 'FARMER', 0, DATE_SUB(NOW(), INTERVAL 7 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo_seed_farmer_pending');

  -- Mock identities have unshared random BCrypt passwords; they are NOT usable demo login accounts.
  INSERT INTO sys_user (username, password_hash, display_name, role_code, status, created_at)
  SELECT 'demo_seed_farmer_rejected', '$2y$10$UHmz47IFWqsCzbXH05DuNe05NsqFN5zReuS3DnYI2AWT/C8w.sQiC', '演示农户·未通过', 'FARMER', 0, DATE_SUB(NOW(), INTERVAL 20 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'demo_seed_farmer_rejected');

  -- Validate demo user role and locked / approved status.
  IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username='demo_seed_admin' AND role_code='ADMIN' AND status=1) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Conflicting demo_seed user. Stop and inspect before running.';
  END IF;

  -- Validate demo user role and locked / approved status.
  IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username='demo_seed_farmer_sc' AND role_code='FARMER' AND status=1) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Conflicting demo_seed user. Stop and inspect before running.';
  END IF;

  -- Validate demo user role and locked / approved status.
  IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username='demo_seed_farmer_sd' AND role_code='FARMER' AND status=1) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Conflicting demo_seed user. Stop and inspect before running.';
  END IF;

  -- Validate demo user role and locked / approved status.
  IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username='demo_seed_farmer_zj' AND role_code='FARMER' AND status=1) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Conflicting demo_seed user. Stop and inspect before running.';
  END IF;

  -- Validate demo user role and locked / approved status.
  IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username='demo_seed_farmer_pending' AND role_code='FARMER' AND status=0) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Conflicting demo_seed user. Stop and inspect before running.';
  END IF;

  -- Validate demo user role and locked / approved status.
  IF NOT EXISTS (SELECT 1 FROM sys_user WHERE username='demo_seed_farmer_rejected' AND role_code='FARMER' AND status=0) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Conflicting demo_seed user. Stop and inspect before running.';
  END IF;

  -- Demo category
  INSERT INTO product_category (category_name, sort_order, status)
  SELECT '【演示】浆果类', 10, 1
  WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = '【演示】浆果类');

  -- Demo category
  INSERT INTO product_category (category_name, sort_order, status)
  SELECT '【演示】柑橘类', 20, 1
  WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = '【演示】柑橘类');

  -- Demo category
  INSERT INTO product_category (category_name, sort_order, status)
  SELECT '【演示】仁果类', 30, 1
  WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = '【演示】仁果类');

  -- Demo category
  INSERT INTO product_category (category_name, sort_order, status)
  SELECT '【演示】茶叶类', 40, 1
  WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = '【演示】茶叶类');

  -- Fictional regional cultivation base
  INSERT INTO planting_base (base_name, owner_id, province, city, district, address, area_mu, description, status, created_at)
  SELECT '【演示】青岚草莓试验基地', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), '四川省', '成都市', '郫都区', '演示地址（虚构基地，不对应实际地块）', 18.5, '仅供软件界面和溯源业务测试', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM planting_base WHERE base_name = '【演示】青岚草莓试验基地');

  -- Fictional regional cultivation base
  INSERT INTO planting_base (base_name, owner_id, province, city, district, address, area_mu, description, status, created_at)
  SELECT '【演示】锦溪柑橘示范园', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), '四川省', '眉山市', '东坡区', '演示地址（虚构基地，不对应实际地块）', 26.0, '仅供软件界面和溯源业务测试', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM planting_base WHERE base_name = '【演示】锦溪柑橘示范园');

  -- Fictional regional cultivation base
  INSERT INTO planting_base (base_name, owner_id, province, city, district, address, area_mu, description, status, created_at)
  SELECT '【演示】沐阳果品试验园', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), '山东省', '烟台市', '栖霞市', '演示地址（虚构基地，不对应实际地块）', 32.5, '仅供软件界面和溯源业务测试', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM planting_base WHERE base_name = '【演示】沐阳果品试验园');

  -- Fictional regional cultivation base
  INSERT INTO planting_base (base_name, owner_id, province, city, district, address, area_mu, description, status, created_at)
  SELECT '【演示】湖畔茶果示范基地', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj'), '浙江省', '湖州市', '安吉县', '演示地址（虚构基地，不对应实际地块）', 20.0, '仅供软件界面和溯源业务测试', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM planting_base WHERE base_name = '【演示】湖畔茶果示范基地');

  -- Fictional product dossier
  INSERT INTO agri_product (category_id, owner_id, product_name, variety, description, status, created_at)
  SELECT (SELECT id FROM product_category WHERE category_name = '【演示】浆果类'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), '【演示】高架草莓', '红颜草莓', '【演示】模拟农产品档案，仅用于软件测试。', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM agri_product WHERE product_name='【演示】高架草莓');

  -- Fictional product dossier
  INSERT INTO agri_product (category_id, owner_id, product_name, variety, description, status, created_at)
  SELECT (SELECT id FROM product_category WHERE category_name = '【演示】柑橘类'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), '【演示】晚熟柑橘', '杂柑', '【演示】模拟农产品档案，仅用于软件测试。', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM agri_product WHERE product_name='【演示】晚熟柑橘');

  -- Fictional product dossier
  INSERT INTO agri_product (category_id, owner_id, product_name, variety, description, status, created_at)
  SELECT (SELECT id FROM product_category WHERE category_name = '【演示】仁果类'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), '【演示】脆甜苹果', '富士', '【演示】模拟农产品档案，仅用于软件测试。', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM agri_product WHERE product_name='【演示】脆甜苹果');

  -- Fictional product dossier
  INSERT INTO agri_product (category_id, owner_id, product_name, variety, description, status, created_at)
  SELECT (SELECT id FROM product_category WHERE category_name = '【演示】浆果类'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), '【演示】高丛蓝莓', '蓝莓试验品种', '【演示】模拟农产品档案，仅用于软件测试。', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM agri_product WHERE product_name='【演示】高丛蓝莓');

  -- Fictional product dossier
  INSERT INTO agri_product (category_id, owner_id, product_name, variety, description, status, created_at)
  SELECT (SELECT id FROM product_category WHERE category_name = '【演示】茶叶类'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj'), '【演示】绿茶鲜叶', '绿茶试验品种', '【演示】模拟农产品档案，仅用于软件测试。', 1, DATE_SUB(NOW(), INTERVAL 8 MONTH)
  WHERE NOT EXISTS (SELECT 1 FROM agri_product WHERE product_name='【演示】绿茶鲜叶');

  -- Published DEMO batch DEMO-TYD-001
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-001', 'DEMO-TRACE-001', (SELECT id FROM agri_product WHERE product_name = '【演示】高架草莓'), (SELECT id FROM planting_base WHERE base_name = '【演示】青岚草莓试验基地'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 5 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 5 MONTH), INTERVAL 5 DAY), 380.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 5 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 5 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-001' OR trace_code='DEMO-TRACE-001');

  -- Published DEMO batch DEMO-TYD-002
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-002', 'DEMO-TRACE-002', (SELECT id FROM agri_product WHERE product_name = '【演示】晚熟柑橘'), (SELECT id FROM planting_base WHERE base_name = '【演示】锦溪柑橘示范园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 4 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 4 MONTH), INTERVAL 5 DAY), 640.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 4 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 4 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-002' OR trace_code='DEMO-TRACE-002');

  -- Published DEMO batch DEMO-TYD-003
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-003', 'DEMO-TRACE-003', (SELECT id FROM agri_product WHERE product_name = '【演示】脆甜苹果'), (SELECT id FROM planting_base WHERE base_name = '【演示】沐阳果品试验园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 3 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 3 MONTH), INTERVAL 5 DAY), 720.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 3 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 3 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-003' OR trace_code='DEMO-TRACE-003');

  -- Published DEMO batch DEMO-TYD-004
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-004', 'DEMO-TRACE-004', (SELECT id FROM agri_product WHERE product_name = '【演示】绿茶鲜叶'), (SELECT id FROM planting_base WHERE base_name = '【演示】湖畔茶果示范基地'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 5 DAY), 170.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 2 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-004' OR trace_code='DEMO-TRACE-004');

  -- Published DEMO batch DEMO-TYD-005
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-005', 'DEMO-TRACE-005', (SELECT id FROM agri_product WHERE product_name = '【演示】高架草莓'), (SELECT id FROM planting_base WHERE base_name = '【演示】青岚草莓试验基地'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 5 DAY), 420.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-005' OR trace_code='DEMO-TRACE-005');

  -- Published DEMO batch DEMO-TYD-006
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-006', 'DEMO-TRACE-006', (SELECT id FROM agri_product WHERE product_name = '【演示】高丛蓝莓'), (SELECT id FROM planting_base WHERE base_name = '【演示】沐阳果品试验园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 0 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 0 MONTH), INTERVAL 5 DAY), 260.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 0 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 0 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-006' OR trace_code='DEMO-TRACE-006');

  -- Published DEMO batch DEMO-TYD-007
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-007', 'DEMO-TRACE-007', (SELECT id FROM agri_product WHERE product_name = '【演示】晚熟柑橘'), (SELECT id FROM planting_base WHERE base_name = '【演示】锦溪柑橘示范园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 0 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 0 MONTH), INTERVAL 5 DAY), 480.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 0 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 0 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-007' OR trace_code='DEMO-TRACE-007');

  -- Published DEMO batch DEMO-TYD-008
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at, created_at)
  SELECT 'DEMO-TYD-008', 'DEMO-TRACE-008', (SELECT id FROM agri_product WHERE product_name = '【演示】脆甜苹果'), (SELECT id FROM planting_base WHERE base_name = '【演示】沐阳果品试验园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 100 DAY), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 5 DAY), 510.0, 'PUBLISHED', '【演示】模拟已发布批次，非真实产品或已核实的溯源信息。', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 MONTH), INTERVAL 110 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-008' OR trace_code='DEMO-TRACE-008');

  -- Non-public DEMO batch DEMO-TYD-009
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at)
  SELECT 'DEMO-TYD-009', 'DEMO-TRACE-009', (SELECT id FROM agri_product WHERE product_name = '【演示】绿茶鲜叶'), (SELECT id FROM planting_base WHERE base_name = '【演示】湖畔茶果示范基地'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj'), DATE_SUB(CURDATE(), INTERVAL 32 DAY), DATE_ADD(CURDATE(), INTERVAL 38 DAY), 180.0, 'DRAFT', '【演示】非公开批次，用于状态与访问权限测试。', NULL
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-009' OR trace_code='DEMO-TRACE-009');

  -- Non-public DEMO batch DEMO-TYD-010
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at)
  SELECT 'DEMO-TYD-010', 'DEMO-TRACE-010', (SELECT id FROM agri_product WHERE product_name = '【演示】高丛蓝莓'), (SELECT id FROM planting_base WHERE base_name = '【演示】沐阳果品试验园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), DATE_SUB(CURDATE(), INTERVAL 32 DAY), DATE_ADD(CURDATE(), INTERVAL 38 DAY), 180.0, 'SUBMITTED', '【演示】非公开批次，用于状态与访问权限测试。', NULL
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-010' OR trace_code='DEMO-TRACE-010');

  -- Non-public DEMO batch DEMO-TYD-011
  INSERT INTO trace_batch (batch_no, trace_code, product_id, base_id, owner_id, planting_date, planned_harvest_date, estimated_quantity_kg, status, remark, published_at)
  SELECT 'DEMO-TYD-011', 'DEMO-TRACE-011', (SELECT id FROM agri_product WHERE product_name = '【演示】晚熟柑橘'), (SELECT id FROM planting_base WHERE base_name = '【演示】锦溪柑橘示范园'), (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), DATE_SUB(CURDATE(), INTERVAL 32 DAY), DATE_ADD(CURDATE(), INTERVAL 38 DAY), 180.0, 'REJECTED', '【演示】非公开批次，用于状态与访问权限测试。', NULL
  WHERE NOT EXISTS (SELECT 1 FROM trace_batch WHERE batch_no='DEMO-TYD-011' OR trace_code='DEMO-TRACE-011');

  -- All tagged batches must exist to preserve referential integrity
  IF (SELECT COUNT(*) FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011')) <> 11 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='DEMO batch identifier conflict. No changes were committed.';
  END IF;

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-001'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-001'), INTERVAL 6 DAY), 296.4, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-001'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-001'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-001')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-001') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-002'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-002'), INTERVAL 6 DAY), 499.2, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-002'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-002'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-002')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-002') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-003'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-003'), INTERVAL 6 DAY), 561.6, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-003'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-003'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-003')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-003') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-004'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-004'), INTERVAL 6 DAY), 132.6, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-004'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-004'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-004')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-004') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-005'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-005'), INTERVAL 6 DAY), 327.6, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-005'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-005'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-005')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-005') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-006'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-006'), INTERVAL 6 DAY), 202.8, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-006'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-006'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-006')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-006') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-007'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-007'), INTERVAL 6 DAY), 374.4, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-007'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-007'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-007')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-007') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- Mock farm operation linked to published DEMO batch
  INSERT INTO farm_record (batch_id, operation_type, operated_at, description, operator_name, created_by)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008'), 'IRRIGATION', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-008'), INTERVAL 36 DAY), '【演示】日常灌溉记录；未实际发生。', '演示农事员', (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd')
  WHERE NOT EXISTS (SELECT 1 FROM farm_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008') AND operation_type='IRRIGATION' AND description LIKE '【演示】%');

  -- Mock harvested quantity strictly positive
  INSERT INTO harvest_record (batch_id, harvested_at, quantity_kg, grade, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-008'), INTERVAL 6 DAY), 397.8, 'DEMO', '【演示】模拟采收数量，不是实际称重记录。'
  WHERE NOT EXISTS (SELECT 1 FROM harvest_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008') AND remark = '【演示】模拟采收数量，不是实际称重记录。');

  -- No fake laboratory report, keep inspection PENDING
  INSERT INTO quality_inspection (batch_id, inspected_at, inspection_item, inspection_org, report_no, conclusion, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008'), DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-008'), INTERVAL 4 DAY), '【演示】模拟质检登记', NULL, NULL, 'PENDING', '【演示】未送检，无真实检测机构、检测报告或合格结论。'
  WHERE NOT EXISTS (SELECT 1 FROM quality_inspection WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008') AND inspection_item='【演示】模拟质检登记');

  -- Mock packing/dispatch status; no actual shipment
  INSERT INTO circulation_record (batch_id, event_type, event_at, from_place, to_place, operator_name, logistics_no, remark)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008'), 'PACKED', DATE_SUB((SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-008'), INTERVAL 2 DAY), '演示基地', '演示分拣环节', '演示记录员', NULL, '【演示】物流事件，不对应真实发货。'
  WHERE NOT EXISTS (SELECT 1 FROM circulation_record WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008') AND event_type='PACKED' AND remark LIKE '【演示】%');

  -- Simulated approval event, not a real regulatory audit
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'APPROVE', '【演示】模拟后台审批事件，非真实审核结果。', (SELECT published_at FROM trace_batch WHERE batch_no='DEMO-TYD-008')
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-008') AND decision='APPROVE' AND comment LIKE '【演示】%');

  -- A mock rejection event for REJECTED batch
  INSERT INTO batch_audit (batch_id, reviewer_id, decision, comment, reviewed_at)
  SELECT (SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-011'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), 'REJECT', '【演示】资料不完整，模拟驳回流程。', DATE_SUB(NOW(), INTERVAL 1 DAY)
  WHERE NOT EXISTS (SELECT 1 FROM batch_audit WHERE batch_id=(SELECT id FROM trace_batch WHERE batch_no = 'DEMO-TYD-011') AND decision='REJECT' AND comment LIKE '【演示】%');

  -- Origin-profile general information, never lab-measured facts
  INSERT INTO origin_profile (base_id, product_id, climate_info, soil_info, flavor_note, feature_tags, information_source)
  SELECT (SELECT id FROM planting_base WHERE base_name = '【演示】青岚草莓试验基地'), (SELECT id FROM agri_product WHERE product_name = '【演示】高架草莓'), '气候信息为模拟描述，未做环境测量。', '土壤信息为模拟描述，未做土壤检测。', '示例风味说明，不是感官评价结论。', 'DEMO,草莓,模拟', 'DEMO 软件测试生成，非实地调查数据'
  WHERE NOT EXISTS (SELECT 1 FROM origin_profile WHERE base_id=(SELECT id FROM planting_base WHERE base_name = '【演示】青岚草莓试验基地') AND product_id=(SELECT id FROM agri_product WHERE product_name = '【演示】高架草莓'));

  -- Origin-profile general information, never lab-measured facts
  INSERT INTO origin_profile (base_id, product_id, climate_info, soil_info, flavor_note, feature_tags, information_source)
  SELECT (SELECT id FROM planting_base WHERE base_name = '【演示】锦溪柑橘示范园'), (SELECT id FROM agri_product WHERE product_name = '【演示】晚熟柑橘'), '模拟丘陵产区气候背景。', '示例土壤资料，不代表实际地块。', '示例酸甜口味说明。', 'DEMO,柑橘,模拟', 'DEMO 软件测试生成，非实地调查数据'
  WHERE NOT EXISTS (SELECT 1 FROM origin_profile WHERE base_id=(SELECT id FROM planting_base WHERE base_name = '【演示】锦溪柑橘示范园') AND product_id=(SELECT id FROM agri_product WHERE product_name = '【演示】晚熟柑橘'));

  -- Origin-profile general information, never lab-measured facts
  INSERT INTO origin_profile (base_id, product_id, climate_info, soil_info, flavor_note, feature_tags, information_source)
  SELECT (SELECT id FROM planting_base WHERE base_name = '【演示】沐阳果品试验园'), (SELECT id FROM agri_product WHERE product_name = '【演示】脆甜苹果'), '模拟温带果园气候描述。', '示例园地土壤描述。', '示例脆甜口感说明。', 'DEMO,苹果,模拟', 'DEMO 软件测试生成，非实地调查数据'
  WHERE NOT EXISTS (SELECT 1 FROM origin_profile WHERE base_id=(SELECT id FROM planting_base WHERE base_name = '【演示】沐阳果品试验园') AND product_id=(SELECT id FROM agri_product WHERE product_name = '【演示】脆甜苹果'));

  -- Origin-profile general information, never lab-measured facts
  INSERT INTO origin_profile (base_id, product_id, climate_info, soil_info, flavor_note, feature_tags, information_source)
  SELECT (SELECT id FROM planting_base WHERE base_name = '【演示】湖畔茶果示范基地'), (SELECT id FROM agri_product WHERE product_name = '【演示】绿茶鲜叶'), '模拟产地气候信息。', '示例茶园土壤资料。', '示例清香描述。', 'DEMO,茶叶,模拟', 'DEMO 软件测试生成，非实地调查数据'
  WHERE NOT EXISTS (SELECT 1 FROM origin_profile WHERE base_id=(SELECT id FROM planting_base WHERE base_name = '【演示】湖畔茶果示范基地') AND product_id=(SELECT id FROM agri_product WHERE product_name = '【演示】绿茶鲜叶'));

  -- DEMO application has placeholder path only; no genuine evidence uploaded
  INSERT INTO farmer_application (applicant_id, applicant_type, organization_name, contact_name, contact_phone, farming_location, proof_type, proof_file_path, application_note, review_status, reviewer_id, review_comment, submitted_at, reviewed_at)
  SELECT (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc'), 'COOPERATIVE', '【演示】青岚农业合作社', '演示联系人A', '00000000000', '四川省 成都市', 'DEMO_PLACEHOLDER', 'DEMO_ONLY/NO_REAL_FILE/demo_seed_farmer_sc', '【演示】测试申请，仅用于 V1.3 审核界面联调。', 'APPROVED', (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), '【演示】仅模拟审核通过', DATE_SUB(NOW(), INTERVAL 8 MONTH), DATE_SUB(NOW(), INTERVAL 8 MONTH) + INTERVAL 1 DAY
  WHERE NOT EXISTS (SELECT 1 FROM farmer_application WHERE applicant_id=(SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sc') AND application_note='【演示】测试申请，仅用于 V1.3 审核界面联调。');

  -- DEMO application has placeholder path only; no genuine evidence uploaded
  INSERT INTO farmer_application (applicant_id, applicant_type, organization_name, contact_name, contact_phone, farming_location, proof_type, proof_file_path, application_note, review_status, reviewer_id, review_comment, submitted_at, reviewed_at)
  SELECT (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd'), 'INDIVIDUAL', NULL, '演示联系人B', '00000000000', '山东省 烟台市', 'DEMO_PLACEHOLDER', 'DEMO_ONLY/NO_REAL_FILE/demo_seed_farmer_sd', '【演示】测试申请，仅用于 V1.3 审核界面联调。', 'APPROVED', (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), '【演示】仅模拟审核通过', DATE_SUB(NOW(), INTERVAL 8 MONTH), DATE_SUB(NOW(), INTERVAL 8 MONTH) + INTERVAL 1 DAY
  WHERE NOT EXISTS (SELECT 1 FROM farmer_application WHERE applicant_id=(SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_sd') AND application_note='【演示】测试申请，仅用于 V1.3 审核界面联调。');

  -- DEMO application has placeholder path only; no genuine evidence uploaded
  INSERT INTO farmer_application (applicant_id, applicant_type, organization_name, contact_name, contact_phone, farming_location, proof_type, proof_file_path, application_note, review_status, reviewer_id, review_comment, submitted_at, reviewed_at)
  SELECT (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj'), 'COOPERATIVE', '【演示】湖畔茶果合作社', '演示联系人C', '00000000000', '浙江省 湖州市', 'DEMO_PLACEHOLDER', 'DEMO_ONLY/NO_REAL_FILE/demo_seed_farmer_zj', '【演示】测试申请，仅用于 V1.3 审核界面联调。', 'APPROVED', (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), '【演示】仅模拟审核通过', DATE_SUB(NOW(), INTERVAL 8 MONTH), DATE_SUB(NOW(), INTERVAL 8 MONTH) + INTERVAL 1 DAY
  WHERE NOT EXISTS (SELECT 1 FROM farmer_application WHERE applicant_id=(SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_zj') AND application_note='【演示】测试申请，仅用于 V1.3 审核界面联调。');

  -- DEMO application has placeholder path only; no genuine evidence uploaded
  INSERT INTO farmer_application (applicant_id, applicant_type, organization_name, contact_name, contact_phone, farming_location, proof_type, proof_file_path, application_note, review_status, reviewer_id, review_comment, submitted_at, reviewed_at)
  SELECT (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_pending'), 'INDIVIDUAL', NULL, '演示联系人D', '00000000000', '四川省 德阳市', 'DEMO_PLACEHOLDER', 'DEMO_ONLY/NO_REAL_FILE/demo_seed_farmer_pending', '【演示】测试申请，仅用于 V1.3 审核界面联调。', 'PENDING', NULL, NULL, DATE_SUB(NOW(), INTERVAL 6 DAY), NULL
  WHERE NOT EXISTS (SELECT 1 FROM farmer_application WHERE applicant_id=(SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_pending') AND application_note='【演示】测试申请，仅用于 V1.3 审核界面联调。');

  -- DEMO application has placeholder path only; no genuine evidence uploaded
  INSERT INTO farmer_application (applicant_id, applicant_type, organization_name, contact_name, contact_phone, farming_location, proof_type, proof_file_path, application_note, review_status, reviewer_id, review_comment, submitted_at, reviewed_at)
  SELECT (SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_rejected'), 'INDIVIDUAL', NULL, '演示联系人E', '00000000000', '山东省 潍坊市', 'DEMO_PLACEHOLDER', 'DEMO_ONLY/NO_REAL_FILE/demo_seed_farmer_rejected', '【演示】测试申请，仅用于 V1.3 审核界面联调。', 'REJECTED', (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), '【演示】材料不完整，模拟退回', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY) + INTERVAL 1 DAY
  WHERE NOT EXISTS (SELECT 1 FROM farmer_application WHERE applicant_id=(SELECT id FROM sys_user WHERE username = 'demo_seed_farmer_rejected') AND application_note='【演示】测试申请，仅用于 V1.3 审核界面联调。');

  -- Public listing shows only PUBLISHED / effective notices
  INSERT INTO sys_notice (title, content, notice_type, status, is_top, published_at, expires_at, created_by, updated_by)
  SELECT '【演示】田野档案平台试运行说明', '本公告仅为本地测试数据。平台中的模拟基地、批次、生产记录均不对应真实经营主体。', 'GENERAL', 'PUBLISHED', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin')
  WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title='【演示】田野档案平台试运行说明');

  -- Public listing shows only PUBLISHED / effective notices
  INSERT INTO sys_notice (title, content, notice_type, status, is_top, published_at, expires_at, created_by, updated_by)
  SELECT '【演示】农户入驻申请流程示例', '这是演示公告。实际农户入驻功能尚在开发，不应上传真实身份证或经营资质。', 'POLICY', 'PUBLISHED', 0, DATE_SUB(NOW(), INTERVAL 8 DAY), NULL, (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin')
  WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title='【演示】农户入驻申请流程示例');

  -- Public listing shows only PUBLISHED / effective notices
  INSERT INTO sys_notice (title, content, notice_type, status, is_top, published_at, expires_at, created_by, updated_by)
  SELECT '【演示】数据维护通知', '平台现阶段使用模拟数据进行页面和接口联调，不代表真实产品质量或批次审查结论。', 'MAINTENANCE', 'PUBLISHED', 0, DATE_SUB(NOW(), INTERVAL 11 DAY), NULL, (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin')
  WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title='【演示】数据维护通知');

  -- Public listing shows only PUBLISHED / effective notices
  INSERT INTO sys_notice (title, content, notice_type, status, is_top, published_at, expires_at, created_by, updated_by)
  SELECT '【演示】公告草稿示例', '仅供后台草稿列表测试，游客不应看到本条内容。', 'GENERAL', 'DRAFT', 0, NULL, NULL, (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin')
  WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title='【演示】公告草稿示例');

  -- Public listing shows only PUBLISHED / effective notices
  INSERT INTO sys_notice (title, content, notice_type, status, is_top, published_at, expires_at, created_by, updated_by)
  SELECT '【演示】已撤回公告示例', '仅供公告撤回状态测试，游客不应看到本条内容。', 'GENERAL', 'WITHDRAWN', 0, DATE_SUB(NOW(), INTERVAL 15 DAY), NULL, (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin')
  WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title='【演示】已撤回公告示例');

  -- Public listing shows only PUBLISHED / effective notices
  INSERT INTO sys_notice (title, content, notice_type, status, is_top, published_at, expires_at, created_by, updated_by)
  SELECT '【演示】已过期公告示例', '仅供公告过期过滤测试，游客不应看到本条内容。', 'GENERAL', 'PUBLISHED', 0, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin'), (SELECT id FROM sys_user WHERE username = 'demo_seed_admin')
  WHERE NOT EXISTS (SELECT 1 FROM sys_notice WHERE title='【演示】已过期公告示例');

  -- Deliberately no trace_attachment: no files were uploaded.
  -- Deliberately no completeness_result: no scoring service has evaluated these records.
  -- Do not call these fake quality records authoritative inspection results.
  COMMIT;
END$$
DELIMITER ;

CALL seed_tyd_demo_v13();
DROP PROCEDURE IF EXISTS seed_tyd_demo_v13;

-- Read-only verification (published-only stats, no invented totals):
SELECT COUNT(*) AS demo_users FROM sys_user WHERE username LIKE 'demo_seed_%';
SELECT status, COUNT(*) AS batches FROM trace_batch WHERE batch_no LIKE 'DEMO-TYD-%' GROUP BY status;
SELECT status, COUNT(*) AS notices FROM sys_notice WHERE title LIKE '【演示】%' GROUP BY status;
SELECT review_status, COUNT(*) AS applications FROM farmer_application WHERE application_note LIKE '【演示】%' GROUP BY review_status;
SELECT id, title, status FROM sys_notice WHERE status='PUBLISHED' AND published_at<=NOW() AND (expires_at IS NULL OR expires_at>NOW()) AND title LIKE '【演示】%';
