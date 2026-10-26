-- Remove the demo-only records created by 06_seed_demo_data.sql.
-- Run locally ONLY after backing up. Does not touch other rows.
-- Will ROLLBACK on SQL error / foreign key references from later user data.
SET NAMES utf8mb4;
USE agri_traceability;

DELIMITER $$
DROP PROCEDURE IF EXISTS cleanup_tyd_demo_v13$$
CREATE PROCEDURE cleanup_tyd_demo_v13()
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;
  START TRANSACTION;

  DELETE FROM batch_audit WHERE batch_id IN (SELECT id FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011'));
  DELETE FROM circulation_record WHERE batch_id IN (SELECT id FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011'));
  DELETE FROM quality_inspection WHERE batch_id IN (SELECT id FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011'));
  DELETE FROM harvest_record WHERE batch_id IN (SELECT id FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011'));
  DELETE FROM farm_record WHERE batch_id IN (SELECT id FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011'));
  DELETE FROM trace_batch WHERE batch_no IN ('DEMO-TYD-001','DEMO-TYD-002','DEMO-TYD-003','DEMO-TYD-004','DEMO-TYD-005','DEMO-TYD-006','DEMO-TYD-007','DEMO-TYD-008','DEMO-TYD-009','DEMO-TYD-010','DEMO-TYD-011');
  DELETE FROM origin_profile WHERE base_id IN (SELECT id FROM planting_base WHERE base_name IN ('【演示】青岚草莓试验基地','【演示】锦溪柑橘示范园','【演示】沐阳果品试验园','【演示】湖畔茶果示范基地')) AND product_id IN (SELECT id FROM agri_product WHERE product_name IN ('【演示】高架草莓','【演示】晚熟柑橘','【演示】脆甜苹果','【演示】高丛蓝莓','【演示】绿茶鲜叶'));
  DELETE FROM farmer_application WHERE applicant_id IN (SELECT id FROM sys_user WHERE username IN ('demo_seed_admin','demo_seed_farmer_sc','demo_seed_farmer_sd','demo_seed_farmer_zj','demo_seed_farmer_pending','demo_seed_farmer_rejected')) AND application_note='【演示】测试申请，仅用于 V1.3 审核界面联调。';
  DELETE FROM sys_notice WHERE title IN ('【演示】田野档案平台试运行说明','【演示】农户入驻申请流程示例','【演示】数据维护通知','【演示】公告草稿示例','【演示】已撤回公告示例','【演示】已过期公告示例');
  DELETE FROM agri_product WHERE product_name IN ('【演示】高架草莓','【演示】晚熟柑橘','【演示】脆甜苹果','【演示】高丛蓝莓','【演示】绿茶鲜叶');
  DELETE FROM planting_base WHERE base_name IN ('【演示】青岚草莓试验基地','【演示】锦溪柑橘示范园','【演示】沐阳果品试验园','【演示】湖畔茶果示范基地');
  DELETE FROM product_category WHERE category_name IN ('【演示】浆果类','【演示】柑橘类','【演示】仁果类','【演示】茶叶类');
  DELETE FROM sys_user WHERE username IN ('demo_seed_admin','demo_seed_farmer_sc','demo_seed_farmer_sd','demo_seed_farmer_zj','demo_seed_farmer_pending','demo_seed_farmer_rejected');
  COMMIT;
END$$
DELIMITER ;

CALL cleanup_tyd_demo_v13();
DROP PROCEDURE IF EXISTS cleanup_tyd_demo_v13;

SELECT COUNT(*) AS remaining_demo_batches FROM trace_batch WHERE batch_no LIKE 'DEMO-TYD-%';
