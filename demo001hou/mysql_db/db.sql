
CREATE DATABASE IF NOT EXISTS user_role_data;

CREATE TABLE IF NOT EXISTS `user_data` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(128) NOT NULL,
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `department` VARCHAR(255) COMMENT '部门',
    `role` ENUM('admin','dispatcher','farmer','analyst') NOT NULL COMMENT '身份',
    `role_name` ENUM('系统管理','生产调度','片区经理','数据分析'),
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '账号状态',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据表';

-- 3. permissions
CREATE TABLE IF NOT EXISTS `permissions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(64) NOT NULL COMMENT '权限点编码',
    `permission` VARCHAR(128) NOT NULL COMMENT '权限行为',
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
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

CREATE TABLE IF NOT EXISTS `regions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(64) DEFAULT NULL COMMENT '片区/行政区编码',
    `name` VARCHAR(128) DEFAULT NULL COMMENT '片区名字',
    `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '地块',
    `meta` JSON DEFAULT NULL COMMENT '杂余信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_regions_code` (`code`),
    KEY `idx_regions_parent_id` (`parent_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='片区/行政区表';


CREATE TABLE IF NOT EXISTS `resource_overview` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stat_mouth` VARCHAR(7) NOT NULL COMMENT '统计月份，格式 YYYY-MM',
    `farmland` DECIMAL(14,2) DEFAULT NULL COMMENT '用耕地',
    `water` DECIMAL(14,2) DEFAULT NULL COMMENT '用用水',
    `material` DECIMAL(14,2) DEFAULT NULL COMMENT '用农资',
    `labor` DECIMAL(14,2) DEFAULT NULL COMMENT '用人力',
    `machine` DECIMAL(14,2) DEFAULT NULL COMMENT '用器械',
    PRIMARY KEY (`id`),
    KEY `idx_stat_mouth` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农业资源调配总览(五维数据)';


CREATE TABLE IF NOT EXISTS `resource_util_trend` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stat_mouth` VARCHAR(7) NOT NULL COMMENT '统计月份 YYYY-MM，图表横轴月份',
    `land_util_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '耕地利用率',
    `water_util_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '水资源利用率',
    `material_util_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '农资利用率',
    `labor_util_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '人力利用率',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_stat_mouth` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源利用率AI评分趋势表';


CREATE TABLE IF NOT EXISTS `resource_alert` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `alert_type` VARCHAR(50) NOT NULL COMMENT '预警类型：用水预警、农机维护预警、库存预警、方案执行通知',
    `region_name` VARCHAR(64) DEFAULT NULL COMMENT '所属片区：西片区、南片区、东片区',
    `target_name` VARCHAR(128) DEFAULT NULL COMMENT '预警对象：用水配额、播种机C型、化肥库存',
    `alert_level` VARCHAR(20) NOT NULL COMMENT '预警等级：警告warning、高危danger、提示info、成功success',
    `alert_content` TEXT NOT NULL COMMENT '预警完整文字内容',
    `suggest_action` TEXT DEFAULT NULL COMMENT 'AI建议处理方案',
    `is_handle` TINYINT(1) DEFAULT 0 COMMENT '是否已处理 0未处理 1已处理',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预警生成时间',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理完成时间',
    PRIMARY KEY (`id`),
    KEY `idx_region_alert` (`region_name`,`alert_type`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源调配异常预警表';

CREATE TABLE IF NOT EXISTS `farmland_total`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `total_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '总额',
    `used_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '使用额',
    `residue_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '剩余额',
    `reused_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '复用额',
    PRIMARY KEY (`id`)
    )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耕地地块总';

CREATE TABLE IF NOT EXISTS `water_total`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `total_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '总额',
    `used_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '使用额',
    `residue_quota` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '剩余额',
    `all_usage_rate` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '使用率',
    PRIMARY KEY (`id`)
    )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水资源总';


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
    `stat_mouth` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_farmland_block_code` (`block_code`),
    KEY `idx_farmland_region_id` (`region_id`),
    KEY `idx_farmland_stat_mouth` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耕地地块台账';

-- 10. farmland_rotation
CREATE TABLE IF NOT EXISTS `farmland_rotation` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `block_id` BIGINT UNSIGNED NOT NULL COMMENT 'farmland_blocks.id',
    `mouth` DATE DEFAULT NULL COMMENT '统计月份',
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
    `response_payload` JSON DEFAULT NULL COMMENT 'AI优化结果',
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
    `stat_mouth` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_water_quota_region_id` (`region_id`),
    KEY `idx_water_quota_stat_mouth` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用水配额表';

-- 13. water_allocation_results
CREATE TABLE IF NOT EXISTS `water_allocation_analysis` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `alloc_period` VARCHAR(10) NOT NULL COMMENT '分配周期（格式：yyyy-Q1/yyyy-MM，如2026-Q2 季度分配）',
    `region_id` BIGINT UNSIGNED NOT NULL COMMENT '片区ID',
    `total_quota` DECIMAL(14,2) DEFAULT 0 COMMENT '该周期片区总用水配额（万m³）',
    `theoretical_demand` DECIMAL(14,2) DEFAULT 0 COMMENT '理论需水量（万m³）',
    `traditional_usage` DECIMAL(14,2) DEFAULT 0 COMMENT '传统方案用水量（万m³），对应页面传统方案柱状图',
    `water_saving_rate` DECIMAL(6,4) DEFAULT 0 COMMENT '节水率（0-1，如0.1500代表15%节水率）',
    `yield_increase_rate` DECIMAL(6,4) DEFAULT 0 COMMENT '作物增产率（0-1）',
    `unit` VARCHAR(16) DEFAULT '万m³' COMMENT '计量单位',
    `water_saving_irrigation` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-节水灌溉评分（0-100分）',
    `sustainability` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-可持续性评分（0-100分）',
    `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-利用率评分（0-100分）',
    `balance` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-均衡性评分（0-100分）',
    `yield_guarantee` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-产量保障评分（0-100分）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_region` (`region_id`),
    KEY `idx_alloc_period` (`alloc_period`),
    KEY `idx_region_id` (`region_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水资源传统分配与分析单表';

CREATE TABLE IF NOT EXISTS `water_ai_analysis`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT 'ai任务id',
    `region_id` BIGINT UNSIGNED NOT NULL COMMENT '片区ID',
    `request` JSON DEFAULT NULL,
    `result` JSON DEFAULT NULL,
    `water_saving_irrigation` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-节水灌溉评分（0-100分）',
    `sustainability` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-可持续性评分（0-100分）',
    `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-利用率评分（0-100分）',
    `balance` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-均衡性评分（0-100分）',
    `yield_guarantee` DECIMAL(5,2) DEFAULT 0 COMMENT '雷达图-产量保障评分（0-100分）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_region` (`region_id`,`task_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水资源ai分配与分析单表';


-- 14. water_analysis
CREATE TABLE IF NOT EXISTS `water_analysis` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    `year` INT DEFAULT NULL COMMENT '统计年份',
    `analysis_result` JSON DEFAULT NULL COMMENT 'ai结果，包含：实际用水	理论需求	浪费量	浪费率	AI建议	节水潜力',
    PRIMARY KEY (`id`),
    KEY `idx_water_analysis_region` (`region_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用水数据分析结果';

-- 15. seed_inventory
CREATE TABLE IF NOT EXISTS `seed_inventory` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `sku` VARCHAR(64) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(128) DEFAULT NULL COMMENT '农资名字',
    `category` VARCHAR(64) DEFAULT NULL COMMENT '分类',
    `inventory` DECIMAL(14,2) DEFAULT 0 COMMENT '库存',
    `safety_threshold` DECIMAL(14,2) DEFAULT 0 COMMENT '阈值',
    `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
    `stat_mouth` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_seed_inventory_sku` (`sku`),
    KEY `idx_seed_inventory_stat_mouth` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资库存台账';


-- 17. seed_allocation_results
CREATE TABLE IF NOT EXISTS `seed_allocation_results` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `task_id` VARCHAR(64) DEFAULT NULL,
    `request` JSON DEFAULT NULL COMMENT '请求',
    `result` JSON DEFAULT NULL COMMENT '结果',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_seed_allocation_task` (`task_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资 AI 分配结果';

-- 18. seed_forecast
CREATE TABLE IF NOT EXISTS `seed_forecast` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_code` VARCHAR(64) NOT NULL COMMENT '农资产品唯一编码（SKU）',
    `stat_mouth` DATE NOT NULL COMMENT '统计/预测周期，格式yyyy-mm-01（支持月度/季度汇总）',
    `history_consumption` DECIMAL(14,2) DEFAULT 0 COMMENT '该周期历史实际消耗数量（对应页面历史消耗折线）',
    `expected_consumption` DECIMAL(14,2) DEFAULT 0 COMMENT 'AI模型预测消耗数量（对应页面AI预测折线）',
    `confidence` DECIMAL(6,4) DEFAULT NULL COMMENT '预测置信度/准确率（0-1，如0.9500代表95%准确率）',
    `current_inventory` DECIMAL(14,2) DEFAULT 0 COMMENT '当前库存数量（对应页面库存预警值）',
    `safety_threshold` DECIMAL(14,2) DEFAULT 0 COMMENT '安全库存阈值（对应页面雷达图基准值）',
    `gap_quantity` DECIMAL(14,2) DEFAULT 0 COMMENT '库存缺口数量（预测需求-当前库存）',
    `alert_level` TINYINT DEFAULT 1 COMMENT '预警等级：1-正常，2-低于安全阈值，3-严重缺口',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_mouth` (`product_code`, `stat_mouth`),
    KEY `idx_stat_mouth` (`stat_mouth`),
    KEY `idx_alert_level` (`alert_level`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资消耗预测与库存预警表';

-- 19. labor_workers
CREATE TABLE IF NOT EXISTS `labor_workers` (
    `labor_id` VARCHAR(10) PRIMARY KEY COMMENT '劳动力编号，唯一主键',
    `labor_name` VARCHAR(20) NOT NULL COMMENT '劳动力姓名',
    `work_type` VARCHAR(50) NOT NULL COMMENT '工种',
    `skill_level` VARCHAR(20) NOT NULL COMMENT '技能等级（高级/中级/初级）',
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    `daily_salary` DECIMAL(10,2) NOT NULL COMMENT '日薪（单位：元）',
    available_time VARCHAR(20) NOT NULL COMMENT '可用时段（全天/白天/夜间）',
    work_status VARCHAR(20) NOT NULL COMMENT '状态（在岗/请假/离职）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) COMMENT = '农业劳动力资源信息库管理表';

-- 20. labor_schedule
CREATE TABLE IF NOT EXISTS `labor_schedule` (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    task_name VARCHAR(32) NOT NULL COMMENT '农时任务',
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    demand_num INT NOT NULL DEFAULT 0 COMMENT '需求人数',
    supply_num INT NOT NULL DEFAULT 0 COMMENT '供给人数',
    gap_num INT NOT NULL DEFAULT 0 COMMENT '缺口人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_task_name(task_name),
    KEY idx_region_id(region_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能排班任务与结果';

-- 22. equipment
CREATE TABLE IF NOT EXISTS `equipment` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `equipment_code` VARCHAR(64) DEFAULT NULL COMMENT '设备编号',
    `name` VARCHAR(128) DEFAULT NULL COMMENT '设备名称',
    `type` VARCHAR(64) DEFAULT NULL COMMENT '类型',
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    `efficiency` VARCHAR(64) DEFAULT NULL COMMENT '效率描述，如 15亩/h',
    `status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
    `stat_mouth` DATE DEFAULT NULL COMMENT '统计月份（按月汇总，例如 2026-08-01）',
    `score` INT DEFAULT NULL COMMENT '状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_equipment_code` (`equipment_code`),
    KEY `idx_equipment_stat_mouth` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备台账';

-- 23. equipment_maintenance
CREATE TABLE IF NOT EXISTS `equipment_maintenance` (
    cost_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    stat_mouth VARCHAR(7) NOT NULL COMMENT '统计月份，格式：YYYY-MM（如2026-01）',
    total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '当月维护总成本（万元）',
    plan_count INT NOT NULL DEFAULT 0 COMMENT '当月完成的维护计划数量',
    manage_count INT NOT NULL DEFAULT 0 COMMENT '已经完成数量',
    proceed_count INT NOT NULL DEFAULT 0 COMMENT '进行中的数量',
    overdue_count INT NOT NULL DEFAULT 0 COMMENT '逾期的数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_stat_mouth (stat_mouth) COMMENT '月份唯一索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护保养记录';

CREATE TABLE IF NOT EXISTS `machinery_dispatch_result` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    `device_type` VARCHAR(50) NOT NULL COMMENT '农机类型：收割设备、耕作设备、播种设备、灌溉设备、植保设备',
    `number` BIGINT DEFAULT 0 COMMENT '数量',
    `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '设备利用率（百分比，如92.00代表92%）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农机智能调配&最短路径调度结果表';

-- 24. equipment_allocation_results
CREATE TABLE IF NOT EXISTS `equipment_allocation_results` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `task_id` VARCHAR(64) DEFAULT NULL,
    `request` JSON DEFAULT NULL,
    `result` JSON DEFAULT NULL,
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

CREATE TABLE IF NOT EXISTS `dispatch_compare_result` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `yield_benefit` DECIMAL(5,2) NOT NULL COMMENT '产量效益分值',
    `risk_control` DECIMAL(5,2) NOT NULL COMMENT '风险控制分值',
    `soil_protect` DECIMAL(5,2) NOT NULL COMMENT '土壤保护分值',
    `labor_util` DECIMAL(5,2) NOT NULL COMMENT '人力利用分值',
    `material_efficiency` DECIMAL(5,2) NOT NULL COMMENT '农资效率分值',
    `water_save` DECIMAL(5,2) NOT NULL COMMENT '节水能力分值',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工调配数据表';

-- 26. ai_results
CREATE TABLE IF NOT EXISTS `ai_results` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `task_id` VARCHAR(64) NOT NULL,
    `request` JSON DEFAULT NULL,
    `result` JSON DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ai_results_task` (`task_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 模块结果表';

-- 27. predict_yield
CREATE TABLE IF NOT EXISTS `predict_yield` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `stat_mouth` VARCHAR(7) NOT NULL COMMENT '统计月份，格式：YYYY-MM（如2026-01）',
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    `crop` VARCHAR(64) DEFAULT NULL COMMENT '名称',
    `predicted_value` DECIMAL(14,2) DEFAULT NULL COMMENT '数值',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_predict_yield_region_crop` (`region_id`,`crop`),
    KEY `idx_predict_resource_year` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产量预测缓存';

-- 28. predict_resource
CREATE TABLE IF NOT EXISTS `predict_resource` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `stat_mouth` VARCHAR(7) NOT NULL COMMENT '统计月份，格式：YYYY-MM（如2026-01）',
    `region_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '片区id',
    `water_demand` DECIMAL(14,2) DEFAULT NULL COMMENT '需水量',
    `fertilizer_demand` DECIMAL(14,2) DEFAULT NULL COMMENT '农资需求',
    `labor_demand` INT DEFAULT NULL COMMENT '劳动力需求',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_predict_yield_region_crop` (`region_id`),
    KEY `idx_predict_resource_year` (`stat_mouth`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源需求预测缓存';