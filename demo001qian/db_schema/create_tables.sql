
create database 'user_role_data'

CREATE TABLE IF NOT EXISTS `user_data` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(128) NOT NULL,
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `role` ENUM('admin','dispatcher','farmer','analyst') NOT NULL COMMENT '身份',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '账号状态',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据表';

-- 3. permissions
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role` ENUM('admin','dispatcher','farmer','analyst') NOT NULL COMMENT '身份',
  `code` VARCHAR(20) NOT NULL COMMENT '权限点编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permissions_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限点表';


-- 5. role_permissions 
CREATE TABLE IF NOT EXISTS `user_permissions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `permission_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_permissions_user_id` (`user_id`),
  KEY `idx_user_permissions_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-权限关联';

-- 7. login_logs
CREATE TABLE IF NOT EXISTS `login_logs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED DEFAULT NULL,
  `role` ENUM('admin','dispatcher','farmer','analyst') NOT NULL COMMENT '身份',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `logout_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_login_logs_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志';

CREATE TABLE IF NOT EXISTS `regions` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(64) DEFAULT NULL COMMENT '片区/行政区编码',
  `name` VARCHAR(128) DEFAULT NULL,
  `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '地块',
  `meta` JSON DEFAULT NULL COMMENT '杂余信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_regions_code` (`code`),
  KEY `idx_regions_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='片区/行政区表';

-- 9. farmland_blocks
CREATE TABLE IF NOT EXISTS `farmland_blocks` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `block_code` VARCHAR(32) NOT NULL COMMENT '地块编号，例如 F001',
  `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属片区 id',
  `area` DECIMAL(12,2) DEFAULT 0 COMMENT '面积（亩）',
  `soil_type` VARCHAR(64) DEFAULT NULL COMMENT '土壤类型',
  `suitable_crops` JSON DEFAULT NULL COMMENT '适宜作物 JSON 数组',
  `current_crop` VARCHAR(64) DEFAULT NULL COMMENT '当前作物',
  `ownership` VARCHAR(128) DEFAULT NULL COMMENT '权属归属',
  `planting_period` VARCHAR(64) DEFAULT NULL COMMENT '可种植周期',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态，例如 种植中/待播种/休耕',
  `stat_month` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_farmland_block_code` (`block_code`),
  KEY `idx_farmland_region_id` (`region_id`),
  KEY `idx_farmland_stat_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耕地地块台账';

-- 10. farmland_rotation
CREATE TABLE IF NOT EXISTS `farmland_rotation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `block_id` BIGINT UNSIGNED NOT NULL COMMENT 'farmland_blocks.id',
  `year` INT NOT NULL,
  `plan` JSON DEFAULT NULL COMMENT '轮作/休耕计划 JSON',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_farmland_rotation_block_id` (`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮作与休耕规划';

-- 11. farmland_optimize_results
CREATE TABLE IF NOT EXISTS `farmland_optimize_results` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) DEFAULT NULL COMMENT '异步任务 id',
  `request_payload` JSON DEFAULT NULL,
  `result` JSON DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_farmland_optimize_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耕地 AI 优化结果存档';

-- 12. water_quota
CREATE TABLE IF NOT EXISTS `water_quota` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT 'regions.id' COMMENT '片区id',
  `period` VARCHAR(32) DEFAULT NULL COMMENT '例如 2024 或 2024-Q1',
  `quota` DECIMAL(14,2) DEFAULT 0 COMMENT '配额（单位需约定）',
  `used` DECIMAL(14,2) DEFAULT 0 COMMENT '已使用',
  `remain` DECIMAL(14,2) DEFAULT(0) COMMENT '剩余',
  `usage_rate` DECIMAL(6,4) DEFAULT 0 COMMENT '比例，0-1，例如 0.8920',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
  `ai_evaluate` VARCHAR(16) DEFAULT NULL COMMENT 'AI评估结果',
  `stat_month` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_water_quota_region_id` (`region_id`),
  KEY `idx_water_quota_stat_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用水配额表';

-- 13. water_allocation_results
CREATE TABLE IF NOT EXISTS `water_allocation_results` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) DEFAULT NULL,
  `request` JSON DEFAULT NULL COMMENT '请求体',
  `allocation` JSON DEFAULT NULL COMMENT '结果',
  `saved_rate` DECIMAL(6,4) DEFAULT NULL COMMENT '节水率',
  `yield_increase` DECIMAL(6,4) DEFAULT NULL COMMENT '作物增长率',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_water_allocation_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水资源 AI 分配结果';

-- 14. water_analysis
CREATE TABLE IF NOT EXISTS `water_analysis` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `region_id` BIGINT UNSIGNED DEFAULT NULL,
  `start_date` DATE DEFAULT NULL,
  `end_date` DATE DEFAULT NULL,
  `analysis_result` JSON DEFAULT NULL,
  `evaluation_score` INT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_water_analysis_region` (`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用水数据分析结果';

-- 15. seed_inventory
CREATE TABLE IF NOT EXISTS `seed_inventory` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `sku` VARCHAR(64) DEFAULT NULL COMMENT '产品编码',
  `product_name` VARCHAR(128) DEFAULT NULL,
  `category` VARCHAR(64) DEFAULT NULL,
  `inventory` DECIMAL(14,2) DEFAULT 0,
  `safety_threshold` DECIMAL(14,2) DEFAULT 0,
  `status` VARCHAR(32) DEFAULT NULL,
  `stat_month` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_seed_inventory_sku` (`sku`),
  KEY `idx_seed_inventory_stat_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资库存台账';

-- 16. seed_movements
CREATE TABLE IF NOT EXISTS `seed_movements` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `sku` VARCHAR(64) DEFAULT NULL,
  `type` ENUM('IN','OUT') NOT NULL,
  `quantity` DECIMAL(14,2) DEFAULT 0,
  `source` VARCHAR(128) DEFAULT NULL,
  `dest` VARCHAR(128) DEFAULT NULL,
  `operator_id` BIGINT UNSIGNED DEFAULT NULL,
  `remark` TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_seed_movements_sku` (`sku`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资出入库流水';

-- 17. seed_allocation_results
CREATE TABLE IF NOT EXISTS `seed_allocation_results` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) DEFAULT NULL,
  `request` JSON DEFAULT NULL,
  `allocation` JSON DEFAULT NULL,
  `metrics` JSON DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seed_allocation_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资 AI 分配结果';

-- 18. seed_forecast
CREATE TABLE IF NOT EXISTS `seed_forecast` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(128) DEFAULT NULL,
  `month` DATE DEFAULT NULL COMMENT '取 yyyy-mm-01 表示当月',
  `expected_consumption` DECIMAL(14,2) DEFAULT NULL,
  `confidence` DECIMAL(6,4) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_seed_forecast_product_month` (`product_name`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资消耗预测';

-- 19. labor_workers
CREATE TABLE IF NOT EXISTS `labor_workers` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `labor_code` VARCHAR(64) DEFAULT NULL,
  `name` VARCHAR(64) DEFAULT NULL,
  `skills` JSON DEFAULT NULL,
  `region_id` BIGINT UNSIGNED DEFAULT NULL,
  `availability` JSON DEFAULT NULL,
  `status` VARCHAR(32) DEFAULT NULL,
  `stat_month` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
  `contact` VARCHAR(64) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_labor_workers_code` (`labor_code`),
  KEY `idx_labor_workers_stat_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='劳动力资源库';

-- 20. labor_schedule
CREATE TABLE IF NOT EXISTS `labor_schedule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) DEFAULT NULL,
  `request` JSON DEFAULT NULL,
  `schedule_plan` JSON DEFAULT NULL,
  `match_rate` DECIMAL(6,4) DEFAULT NULL,
  `gap_count` INT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_labor_schedule_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能排班任务与结果';

-- 21. labor_assignments
CREATE TABLE IF NOT EXISTS `labor_assignments` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `schedule_id` BIGINT UNSIGNED NOT NULL,
  `labor_id` BIGINT UNSIGNED NOT NULL,
  `date` DATE NOT NULL,
  `hours` DECIMAL(5,2) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_labor_assignments_schedule` (`schedule_id`),
  KEY `idx_labor_assignments_labor` (`labor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班具体分配项';

-- 22. equipment
CREATE TABLE IF NOT EXISTS `equipment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `equipment_code` VARCHAR(64) DEFAULT NULL,
  `name` VARCHAR(128) DEFAULT NULL,
  `type` VARCHAR(64) DEFAULT NULL,
  `area` VARCHAR(64) DEFAULT NULL,
  `efficiency` VARCHAR(64) DEFAULT NULL COMMENT '效率描述，如 15亩/h',
  `status` VARCHAR(32) DEFAULT NULL,
  `stat_month` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
  `score` INT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equipment_code` (`equipment_code`),
  KEY `idx_equipment_stat_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备台账';

-- 23. equipment_maintenance
CREATE TABLE IF NOT EXISTS `equipment_maintenance` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `equipment_id` BIGINT UNSIGNED NOT NULL,
  `last_maintenance` DATE DEFAULT NULL,
  `next_maintenance` DATE DEFAULT NULL,
  `status` VARCHAR(64) DEFAULT NULL,
  `overdue_days` INT DEFAULT 0,
  `cost` DECIMAL(12,2) DEFAULT 0,
  `remark` TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_equipment_maintenance_eid` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护保养记录';

-- 24. equipment_allocation_results
CREATE TABLE IF NOT EXISTS `equipment_allocation_results` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) DEFAULT NULL,
  `request` JSON DEFAULT NULL,
  `routes` JSON DEFAULT NULL,
  `metrics` JSON DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_equipment_allocation_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备路径优化/调配结果';

-- 25. tasks
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) NOT NULL COMMENT '外部可见的任务 id',
  `type` VARCHAR(64) DEFAULT NULL COMMENT '任务类型，例如 ai_optimize',
  `status` VARCHAR(32) DEFAULT 'pending' COMMENT 'pending/running/success/failed',
  `request_payload` JSON DEFAULT NULL,
  `result` JSON DEFAULT NULL,
  `progress` DECIMAL(5,2) DEFAULT 0,
  `error_message` TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `finished_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tasks_taskid` (`task_id`),
  KEY `idx_tasks_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用任务表（异步任务管理）';

-- 26. ai_results
CREATE TABLE IF NOT EXISTS `ai_results` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` VARCHAR(64) NOT NULL,
  `module` VARCHAR(64) DEFAULT NULL,
  `result` JSON DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_results_task` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 模块结果表';

-- 27. predict_yield
CREATE TABLE IF NOT EXISTS `predict_yield` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `year` INT NOT NULL,
  `region_id` BIGINT UNSIGNED DEFAULT NULL,
  `crop` VARCHAR(64) DEFAULT NULL,
  `predicted_value` DECIMAL(14,2) DEFAULT NULL,
  `confidence` DECIMAL(6,4) DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_predict_yield_year_region_crop` (`year`,`region_id`,`crop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产量预测缓存';

-- 28. predict_resource
CREATE TABLE IF NOT EXISTS `predict_resource` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `year` INT NOT NULL,
  `water_demand` DECIMAL(14,2) DEFAULT NULL,
  `fertilizer_demand` DECIMAL(14,2) DEFAULT NULL,
  `labor_demand` INT DEFAULT NULL,
  `details` JSON DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_predict_resource_year` (`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源需求预测缓存';
