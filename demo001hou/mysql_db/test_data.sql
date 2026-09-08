-- ============================================
-- 农业管理系统 - 测试数据 SQL
-- 数据库: user_role_data
-- ============================================

USE user_role_data;

-- ============================================
-- 1. 区域/片区数据 (regions)
-- ============================================
INSERT INTO `regions` (`id`, `code`, `name`, `parent_id`, `meta`) VALUES
    (1, 'R-EAST',    '东片区', NULL, '{"description": "东部种植区", "area": 1500}'),
    (2, 'R-WEST',    '西片区', NULL, '{"description": "西部种植区", "area": 1200}'),
    (3, 'R-SOUTH',   '南片区', NULL, '{"description": "南部种植区", "area": 1000}'),
    (4, 'R-NORTH',   '北片区', NULL, '{"description": "北部种植区", "area": 800}')
    ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ============================================
-- 2. 用户数据 (user_data)
-- ============================================
INSERT INTO `user_data` (`id`, `username`, `password`, `department`, `role`, `role_name`, `status`, `created_time`) VALUES
    (1, 'admin',      'admin123', '管理部', 'admin',      '系统管理', 1, NOW()),
    (2, 'dispatcher', 'disp123',   '调度部', 'dispatcher', '生产调度', 1, NOW()),
    (3, 'farmer',     'farmer123', '种植部', 'farmer',     '片区经理', 1, NOW()),
    (4, 'analyst',    'analyst123','分析部', 'analyst',    '数据分析', 1, NOW())
    ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

-- ============================================
-- 3. 权限数据 (permissions)
-- ============================================
INSERT INTO `permissions` (`id`, `code`, `permission`) VALUES
    (1,  'dashboard:view',    '查看看板'),
    (2,  'farmland:view',     '查看耕地'),
    (3,  'farmland:create',   '创建耕地'),
    (4,  'farmland:update',   '更新耕地'),
    (5,  'farmland:delete',   '删除耕地'),
    (6,  'water:view',        '查看水资源'),
    (7,  'water:update',      '更新水资源'),
    (8,  'seed:view',         '查看农资'),
    (9,  'seed:create',       '创建农资'),
    (10, 'seed:update',       '更新农资'),
    (11, 'seed:delete',       '删除农资'),
    (12, 'labor:view',        '查看劳动力'),
    (13, 'labor:create',      '创建劳动力'),
    (14, 'labor:update',      '更新劳动力'),
    (15, 'labor:delete',      '删除劳动力'),
    (16, 'equipment:view',    '查看设备'),
    (17, 'equipment:create',  '创建设备'),
    (18, 'equipment:update',  '更新设备'),
    (19, 'equipment:delete',  '删除设备'),
    (20, 'system:manage',     '系统管理'),
    (21, 'ai:decision',       'AI决策'),
    (22, 'farmland:optimize', 'AI种植结构优化'),
    (23, 'farmland:rotation', '轮作休耕规划'),
    (24, 'water:allocate',    'AI水量优化分配'),
    (25, 'water:analysis',    '用水分析'),
    (26, 'water:quota:view',  '查看用水配额'),
    (27, 'water:quota:update','更新用水配额'),
    (28, 'seed:allocate',     'AI按需分配算法'),
    (29, 'seed:predict',      '消耗预测与库存预警'),
    (30, 'labor:schedule:view','查看劳动力排班'),
    (31, 'labor:schedule:manage','劳动力排班管理'),
    (32, 'equipment:status',  '设备状态查看'),
    (33, 'equipment:allocate','AI设备调度'),
    (34, 'equipment:maintain','设备维护'),
    (35, 'predict:yield',     '产量预测'),
    (36, 'predict:resource',  '资源需求预测'),
    (37, 'system:users',      '系统用户管理'),
    (38, 'system:create',     '系统用户创建')
    ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- ============================================
-- 4. 用户-权限关联 (user_permissions)
-- ============================================
INSERT INTO `user_permissions` (`id`, `user_id`, `permission_id`) VALUES
    (1,  1, 1), (2,  1, 2), (3,  1, 3), (4,  1, 4), (5,  1, 5),
    (6,  1, 6), (7,  1, 7), (8,  1, 8), (9,  1, 9), (10, 1, 10),
    (11, 1, 11),(12, 1, 12),(13, 1, 13),(14, 1, 14),(15, 1, 15),
    (16, 1, 16),(17, 1, 17),(18, 1, 18),(19, 1, 19),(20, 1, 20),(21, 1, 21),
    (22, 1, 22),(23, 1, 23),(24, 1, 24),(25, 1, 25),(26, 1, 26),
    (27, 1, 27),(28, 1, 28),(29, 1, 29),(30, 1, 30),(31, 1, 31),
    (32, 1, 32),(33, 1, 33),(34, 1, 34),(35, 1, 35),(36, 1, 36),
    (37, 1, 37),(38, 1, 38),
    (39, 2, 1),(40, 2, 2),(41, 2, 6),(42, 2, 8),(43, 2, 12),(44, 2, 16),
    (45, 3, 1),(46, 3, 2),(47, 3, 6),(48, 3, 8),(49, 3, 12),(50, 3, 16),
    (51, 4, 1),(52, 4, 21)
    ON DUPLICATE KEY UPDATE `permission_id` = VALUES(`permission_id`);

-- ============================================
-- 5. 登录日志 (login_logs)
-- ============================================
INSERT INTO `login_logs` (`id`, `user_id`, `role`, `created_time`, `logout_time`) VALUES
    (1, 1, 'admin',      '2026-09-01 08:00:00', '2026-09-01 18:00:00'),
    (2, 2, 'dispatcher', '2026-09-01 08:30:00', '2026-09-01 17:30:00'),
    (3, 3, 'farmer',     '2026-09-01 07:00:00', '2026-09-01 16:00:00'),
    (4, 4, 'analyst',    '2026-09-01 09:00:00', '2026-09-01 18:00:00'),
    (5, 1, 'admin',      '2026-09-02 08:00:00', NULL)
    ON DUPLICATE KEY UPDATE `role` = VALUES(`role`);

-- ============================================
-- 6. 耕地地块汇总 (farmland_total)
-- ============================================
INSERT INTO `farmland_total` (`id`, `total_quota`, `used_quota`, `residue_quota`, `reused_quota`) VALUES
    (1, 4500.00, 3200.00, 800.00, 500.00)
    ON DUPLICATE KEY UPDATE `total_quota` = VALUES(`total_quota`);

-- ============================================
-- 7. 水资源汇总 (water_total)
-- ============================================
INSERT INTO `water_total` (`id`, `total_quota`, `used_quota`, `residue_quota`, `all_usage_rate`) VALUES
    (1, 10000.00, 7200.00, 2800.00, 72.00)
    ON DUPLICATE KEY UPDATE `total_quota` = VALUES(`total_quota`);

-- ============================================
-- 8. 耕地地块台账 (farmland_blocks)
-- ============================================
INSERT INTO `farmland_blocks` (`id`, `block_code`, `region_id`, `area`, `soil_type`, `suitable_crops`, `current_crop`, `ownership`, `planting_period`, `status`, `stat_mouth`, `created_at`, `updated_at`) VALUES
    (1,  'F001', 1, 120.50, '黑土', '["水稻","小麦","玉米"]', '水稻', '张三', '3月-9月', '种植中', '2026-09-01', NOW(), NOW()),
    (2,  'F002', 1, 85.00,  '壤土', '["小麦","玉米","大豆"]', '玉米', '李四', '4月-10月', '种植中', '2026-09-01', NOW(), NOW()),
    (3,  'F003', 2, 200.00, '沙土', '["棉花","花生","红薯"]', '棉花', '王五', '5月-11月', '种植中', '2026-09-01', NOW(), NOW()),
    (4,  'F004', 2, 150.00, '黏土', '["水稻","莲藕"]', '水稻', '赵六', '4月-10月', '待播种', '2026-09-01', NOW(), NOW()),
    (5,  'F005', 3, 95.00,  '黑土', '["大豆","玉米","高粱"]', '大豆', '钱七', '5月-10月', '种植中', '2026-09-01', NOW(), NOW()),
    (6,  'F006', 3, 180.00, '壤土', '["小麦","油菜","蔬菜"]', '小麦', '孙八', '3月-8月', '休耕', '2026-09-01', NOW(), NOW()),
    (7,  'F007', 4, 110.00, '沙土', '["果树","茶叶","中草药"]', '果树', '周九', '全年', '种植中', '2026-09-01', NOW(), NOW()),
    (8,  'F008', 4, 75.00,  '黏土', '["水稻","茭白"]', '水稻', '吴十', '5月-10月', '种植中', '2026-09-01', NOW(), NOW()),
    (9,  'F009', 1, 60.00,  '壤土', '["蔬菜","草莓","瓜类"]', '蔬菜', '郑十一', '3月-11月', '种植中', '2026-09-01', NOW(), NOW()),
    (10, 'F010', 2, 130.00, '黑土', '["玉米","大豆","水稻"]', '玉米', '冯十二', '4月-10月', '待播种', '2026-09-01', NOW(), NOW())
    ON DUPLICATE KEY UPDATE `block_code` = VALUES(`block_code`);

-- ============================================
-- 9. 轮作休耕规划 (farmland_rotation)
-- ============================================
INSERT INTO `farmland_rotation` (`id`, `block_id`, `mouth`, `plan`, `created_at`) VALUES
    (1,  1,  '2026-01-01', '{"水稻": 40, "小麦": 35, "休耕": 25}', NOW()),
    (2,  1,  '2026-02-01', '{"水稻": 45, "小麦": 30, "休耕": 25}', NOW()),
    (3,  1,  '2026-03-01', '{"水稻": 50, "小麦": 30, "休耕": 20}', NOW()),
    (4,  2,  '2026-01-01', '{"玉米": 50, "大豆": 30, "休耕": 20}', NOW()),
    (5,  2,  '2026-02-01', '{"玉米": 45, "大豆": 35, "休耕": 20}', NOW()),
    (6,  3,  '2026-01-01', '{"棉花": 60, "花生": 25, "休耕": 15}', NOW()),
    (7,  4,  '2026-01-01', '{"水稻": 55, "莲藕": 30, "休耕": 15}', NOW()),
    (8,  5,  '2026-01-01', '{"大豆": 45, "玉米": 30, "休耕": 25}', NOW()),
    (9,  6,  '2026-01-01', '{"小麦": 40, "油菜": 30, "休耕": 30}', NOW()),
    (10, 7,  '2026-01-01', '{"果树": 70, "茶叶": 20, "休耕": 10}', NOW())
    ON DUPLICATE KEY UPDATE `plan` = VALUES(`plan`);

-- ============================================
-- 10. 耕地AI优化结果 (farmland_optimize_results)
-- ============================================
INSERT INTO `farmland_optimize_results` (`id`, `task_id`, `request_payload`, `created_at`) VALUES
    (1, 'opt-task-001', '{"goal": "产量最大化", "constraintMode": "节水优先", "year": 2026, "blockCount": 10}', NOW()),
    (2, 'opt-task-002', '{"goal": "效益最大化", "constraintMode": "生态保护", "year": 2026, "blockCount": 8}', NOW())
    ON DUPLICATE KEY UPDATE `task_id` = VALUES(`task_id`);

-- ============================================
-- 11. 用水配额表 (water_quota)
-- ============================================
INSERT INTO `water_quota` (`id`, `region_id`, `period`, `quota`, `used`, `remain`, `usage_rate`, `status`, `ai_evaluate`, `stat_mouth`, `updated_at`) VALUES
    (1,  1, '2026-Q3', 3000.00, 2200.00, 800.00,  0.7333, '正常', '良', '2026-09-01', NOW()),
    (2,  2, '2026-Q3', 2500.00, 1800.00, 700.00,  0.7200, '正常', '良', '2026-09-01', NOW()),
    (3,  3, '2026-Q3', 2200.00, 1900.00, 300.00,  0.8636, '预警', '差', '2026-09-01', NOW()),
    (4,  4, '2026-Q3', 2300.00, 1300.00, 1000.00, 0.5652, '正常', '优', '2026-09-01', NOW()),
    (5,  1, '2026-Q2', 2800.00, 2000.00, 800.00,  0.7143, '正常', '良', '2026-06-01', NOW()),
    (6,  2, '2026-Q2', 2400.00, 1600.00, 800.00,  0.6667, '正常', '良', '2026-06-01', NOW()),
    (7,  3, '2026-Q2', 2000.00, 1650.00, 350.00,  0.8250, '正常', '中', '2026-06-01', NOW()),
    (8,  4, '2026-Q2', 2100.00, 1100.00, 1000.00, 0.5238, '正常', '优', '2026-06-01', NOW())
    ON DUPLICATE KEY UPDATE `quota` = VALUES(`quota`);

-- ============================================
-- 12. 水资源传统分配分析 (water_allocation_analysis)
-- ============================================
INSERT INTO `water_allocation_analysis` (`id`, `alloc_period`, `region_id`, `total_quota`, `theoretical_demand`, `traditional_usage`, `water_saving_rate`, `yield_increase_rate`, `unit`, `water_saving_irrigation`, `sustainability`, `utilization_rate`, `balance`, `yield_guarantee`, `created_at`, `updated_at`) VALUES
    (1, '2026-Q3', 1, 3000.00, 2800.00, 3100.00, 0.1500, 0.1200, '万m³', 88.00, 85.00, 90.00, 82.00, 92.00, NOW(), NOW()),
    (2, '2026-Q3', 2, 2500.00, 2300.00, 2600.00, 0.1200, 0.1000, '万m³', 85.00, 88.00, 92.00, 80.00, 90.00, NOW(), NOW()),
    (3, '2026-Q3', 3, 2200.00, 2000.00, 2350.00, 0.1800, 0.1500, '万m³', 82.00, 80.00, 78.00, 75.00, 88.00, NOW(), NOW()),
    (4, '2026-Q3', 4, 2300.00, 2100.00, 2200.00, 0.1000, 0.0800, '万m³', 90.00, 92.00, 95.00, 88.00, 94.00, NOW(), NOW())
    ON DUPLICATE KEY UPDATE `alloc_period` = VALUES(`alloc_period`);

-- ============================================
-- 13. 水资源AI分配分析 (water_ai_analysis)
-- ============================================
INSERT INTO `water_ai_analysis` (`id`, `task_id`, `region_id`, `request`, `result`, `water_saving_irrigation`, `sustainability`, `utilization_rate`, `balance`, `yield_guarantee`, `created_at`) VALUES
    (1, 1, 1, '{"cycle": "2026-Q3", "goal": "节水最大化"}', '{"plan": [{"area": "东片区", "value": 2500}], "waterSaved": 12.5, "yieldIncrease": 8.3}', 92.00, 88.00, 93.00, 85.00, 95.00, NOW()),
    (2, 2, 2, '{"cycle": "2026-Q3", "goal": "节水最大化"}', '{"plan": [{"area": "西片区", "value": 2100}], "waterSaved": 10.0, "yieldIncrease": 7.5}', 89.00, 90.00, 91.00, 83.00, 92.00, NOW())
    ON DUPLICATE KEY UPDATE `task_id` = VALUES(`task_id`);

-- ============================================
-- 14. 用水数据分析 (water_analysis)
-- ============================================
INSERT INTO `water_analysis` (`id`, `region_id`, `year`, `analysis_result`) VALUES
    (1, 1, 2026, '{"actualUsage": 2200, "theoreticalDemand": 2000, "wasteAmount": 200, "wasteRate": 0.09, "aiSuggestion": "建议采用滴灌技术，预计可节水15%", "savingPotential": 330, "mouthlyActual": [150,160,180,200,220,240,230,210,190,170,150,100], "mouthlyPredicted": [140,150,170,190,210,230,220,200,180,160,140,95]}'),
    (2, 2, 2026, '{"actualUsage": 1800, "theoreticalDemand": 1650, "wasteAmount": 150, "wasteRate": 0.083, "aiSuggestion": "优化灌溉时段，减少蒸发损失", "savingPotential": 250, "mouthlyActual": [120,130,150,170,190,200,180,160,140,120,110,90], "mouthlyPredicted": [115,125,145,160,185,195,175,155,135,115,105,85]}'),
    (3, 3, 2026, '{"actualUsage": 1900, "theoreticalDemand": 1600, "wasteAmount": 300, "wasteRate": 0.158, "aiSuggestion": "严重浪费区域，需立即整改灌溉系统", "savingPotential": 400, "mouthlyActual": [130,140,160,180,200,210,190,170,150,130,120,120], "mouthlyPredicted": [125,135,150,170,190,195,180,160,140,125,110,105]}'),
    (4, 4, 2026, '{"actualUsage": 1300, "theoreticalDemand": 1250, "wasteAmount": 50, "wasteRate": 0.038, "aiSuggestion": "用水效率较高，保持当前方案", "savingPotential": 80, "mouthlyActual": [100,110,120,130,140,150,130,120,100,90,80,70], "mouthlyPredicted": [95,105,115,125,135,148,128,118,98,88,78,68]}')
    ON DUPLICATE KEY UPDATE `year` = VALUES(`year`);

-- ============================================
-- 15. 农资库存台账 (seed_inventory)
-- ============================================
INSERT INTO `seed_inventory` (`id`, `sku`, `product_name`, `category`, `inventory`, `safety_threshold`, `status`, `stat_mouth`, `updated_at`) VALUES
    (1,  'SKU-FHF-001', '复合肥', '化肥', 5000.00, 1000.00, '正常', '2026-09-01', NOW()),
    (2,  'SKU-NS-001',  '尿素',   '化肥', 3000.00, 800.00,  '正常', '2026-09-01', NOW()),
    (3,  'SKU-SCJ-001', '杀虫剂', '农药', 800.00,  200.00,  '预警', '2026-09-01', NOW()),
    (4,  'SKU-SD-001',  '水稻种子','种子', 1200.00, 300.00,  '正常', '2026-09-01', NOW()),
    (5,  'SKU-XM-001',  '小麦种子','种子', 900.00,  250.00,  '正常', '2026-09-01', NOW()),
    (6,  'SKU-YM-001',  '玉米种子','种子', 600.00,  200.00,  '预警', '2026-09-01', NOW()),
    (7,  'SKU-CJ-001',  '除草剂', '农药', 450.00,  150.00,  '正常', '2026-09-01', NOW()),
    (8,  'SKU-YJ-001',  '有机肥', '化肥', 2000.00, 500.00,  '正常', '2026-09-01', NOW())
    ON DUPLICATE KEY UPDATE `sku` = VALUES(`sku`);

-- ============================================
-- 16. 农资AI分配结果 (seed_allocation_results)
-- ============================================
INSERT INTO `seed_allocation_results` (`id`, `task_id`, `request`, `result`, `created_at`) VALUES
    (1, 'seed-task-001', '{"crop": "水稻", "strategy": "按种植面积比例"}', '{"categories": ["复合肥","尿素","杀虫剂","种子"], "legend": ["水稻","小麦","玉米","大豆"], "yAxisName": "kg", "series": [{"name": "水稻", "type": "bar", "stack": "a", "data": [850, 620, 25, 35]}], "tips": "化肥利用率提升 12.5%\\n成本降低: 8.6%"}', NOW()),
    (2, 'seed-task-002', '{"crop": "全部作物", "strategy": "按需分配"}', '{"categories": ["复合肥","尿素","杀虫剂","种子"], "legend": ["水稻","小麦","玉米","大豆"], "yAxisName": "kg", "series": [{"name": "小麦", "type": "bar", "stack": "a", "data": [720, 540, 18, 28]}], "tips": "农药减量 15%\\n产量提升: 10.2%"}', NOW())
    ON DUPLICATE KEY UPDATE `task_id` = VALUES(`task_id`);

-- ============================================
-- 17. 农资消耗预测与库存预警 (seed_forecast)
-- ============================================
INSERT INTO `seed_forecast` (`id`, `product_code`, `stat_mouth`, `history_consumption`, `expected_consumption`, `confidence`, `current_inventory`, `safety_threshold`, `gap_quantity`, `alert_level`, `created_at`, `updated_at`) VALUES
    (1, 'SKU-FHF-001', '2026-01-01', 380.00, 400.00, 0.95, 5200.00, 1000.00, 0,    1, NOW(), NOW()),
    (2, 'SKU-FHF-001', '2026-02-01', 350.00, 380.00, 0.94, 5100.00, 1000.00, 0,    1, NOW(), NOW()),
    (3, 'SKU-FHF-001', '2026-03-01', 420.00, 450.00, 0.93, 5000.00, 1000.00, 0,    1, NOW(), NOW()),
    (4, 'SKU-FHF-001', '2026-04-01', 450.00, 480.00, 0.92, 4800.00, 1000.00, 0,    1, NOW(), NOW()),
    (5, 'SKU-FHF-001', '2026-05-01', 480.00, 520.00, 0.91, 4600.00, 1000.00, 0,    1, NOW(), NOW()),
    (6, 'SKU-FHF-001', '2026-06-01', 500.00, 550.00, 0.90, 4400.00, 1000.00, 0,    1, NOW(), NOW()),
    (7, 'SKU-FHF-001', '2026-07-01', 520.00, 580.00, 0.89, 4200.00, 1000.00, 0,    1, NOW(), NOW()),
    (8, 'SKU-FHF-001', '2026-08-01', 550.00, 600.00, 0.88, 4000.00, 1000.00, 0,    1, NOW(), NOW()),
    (9, 'SKU-NS-001',  '2026-01-01', 250.00, 270.00, 0.95, 3100.00, 800.00,  0,    1, NOW(), NOW()),
    (10,'SKU-NS-001',  '2026-02-01', 230.00, 250.00, 0.94, 3050.00, 800.00,  0,    1, NOW(), NOW()),
    (11,'SKU-NS-001',  '2026-03-01', 280.00, 300.00, 0.93, 3000.00, 800.00,  0,    1, NOW(), NOW()),
    (12,'SKU-NS-001',  '2026-04-01', 300.00, 320.00, 0.92, 2900.00, 800.00,  0,    1, NOW(), NOW()),
    (13,'SKU-SCJ-001', '2026-01-01', 60.00,  65.00,  0.93, 850.00,  200.00,  0,    1, NOW(), NOW()),
    (14,'SKU-SCJ-001', '2026-02-01', 55.00,  60.00,  0.92, 830.00,  200.00,  0,    1, NOW(), NOW()),
    (15,'SKU-SCJ-001', '2026-03-01', 70.00,  75.00,  0.91, 800.00,  200.00,  0,    1, NOW(), NOW()),
    (16,'SKU-SCJ-001', '2026-04-01', 80.00,  85.00,  0.90, 780.00,  200.00,  0,    1, NOW(), NOW())
    ON DUPLICATE KEY UPDATE `product_code` = VALUES(`product_code`);

-- ============================================
-- 18. 劳动力信息表 (labor_workers)
-- ============================================
INSERT INTO `labor_workers` (`labor_id`, `labor_name`, `work_type`, `skill_level`, `region_id`, `daily_salary`, `available_time`, `work_status`, `create_time`, `update_time`) VALUES
    ('L001', '张师傅', '种植',   '高级', 1, 200.00, '全天', '在岗', NOW(), NOW()),
    ('L002', '李师傅', '种植',   '中级', 1, 150.00, '白天', '在岗', NOW(), NOW()),
    ('L003', '王师傅', '灌溉',   '高级', 2, 220.00, '全天', '在岗', NOW(), NOW()),
    ('L004', '赵师傅', '施肥',   '中级', 2, 160.00, '白天', '在岗', NOW(), NOW()),
    ('L005', '钱师傅', '收割',   '高级', 3, 250.00, '全天', '在岗', NOW(), NOW()),
    ('L006', '孙师傅', '种植',   '初级', 3, 120.00, '全天', '在岗', NOW(), NOW()),
    ('L007', '周师傅', '植保',   '中级', 4, 180.00, '白天', '在岗', NOW(), NOW()),
    ('L008', '吴师傅', '农机',   '高级', 4, 280.00, '全天', '在岗', NOW(), NOW()),
    ('L009', '郑师傅', '种植',   '中级', 1, 150.00, '白天', '请假', NOW(), NOW()),
    ('L010', '冯师傅', '灌溉',   '初级', 2, 130.00, '全天', '在岗', NOW(), NOW())
    ON DUPLICATE KEY UPDATE `labor_name` = VALUES(`labor_name`);

-- ============================================
-- 19. 劳动力排班表 (labor_schedule)
-- ============================================
INSERT INTO `labor_schedule` (`id`, `task_name`, `region_id`, `demand_num`, `supply_num`, `gap_num`, `create_time`, `update_time`) VALUES
    (1, '春耕播种', 1, 300, 280, 20,  NOW(), NOW()),
    (2, '春耕播种', 2, 250, 240, 10,  NOW(), NOW()),
    (3, '夏种管理', 1, 200, 190, 10,  NOW(), NOW()),
    (4, '夏种管理', 3, 180, 170, 10,  NOW(), NOW()),
    (5, '秋收作业', 1, 350, 340, 10,  NOW(), NOW()),
    (6, '秋收作业', 2, 300, 290, 10,  NOW(), NOW()),
    (7, '秋收作业', 3, 280, 270, 10,  NOW(), NOW()),
    (8, '冬藏整地', 4, 150, 145, 5,   NOW(), NOW())
    ON DUPLICATE KEY UPDATE `task_name` = VALUES(`task_name`);

-- ============================================
-- 20. 设备台账 (equipment)
-- ============================================
INSERT INTO `equipment` (`id`, `equipment_code`, `name`, `type`, `region_id`, `efficiency`, `status`, `stat_mouth`, `score`, `created_at`, `updated_at`) VALUES
    (1,  'EQ-001', '收割机A型', '收割设备', 1, '15亩/h', '正常', '2026-09-01', 85, NOW(), NOW()),
    (2,  'EQ-002', '播种机B型', '播种设备', 1, '10亩/h', '正常', '2026-09-01', 90, NOW(), NOW()),
    (3,  'EQ-003', '拖拉机C型', '耕作设备', 2, '8亩/h',  '正常', '2026-09-01', 78, NOW(), NOW()),
    (4,  'EQ-004', '灌溉设备D型','灌溉设备', 2, '20亩/h', '正常', '2026-09-01', 92, NOW(), NOW()),
    (5,  'EQ-005', '植保无人机E', '植保设备', 3, '30亩/h', '正常', '2026-09-01', 88, NOW(), NOW()),
    (6,  'EQ-006', '播种机F型', '播种设备', 3, '12亩/h', '维修中', '2026-09-01', 65, NOW(), NOW()),
    (7,  'EQ-007', '收割机G型', '收割设备', 4, '18亩/h', '正常', '2026-09-01', 91, NOW(), NOW()),
    (8,  'EQ-008', '耕作机H型', '耕作设备', 4, '9亩/h',  '亚健康', '2026-09-01', 72, NOW(), NOW()),
    (9,  'EQ-009', '灌溉设备I型','灌溉设备', 1, '22亩/h', '正常', '2026-09-01', 94, NOW(), NOW()),
    (10, 'EQ-010', '植保无人机J', '植保设备', 2, '28亩/h', '正常', '2026-09-01', 87, NOW(), NOW())
    ON DUPLICATE KEY UPDATE `equipment_code` = VALUES(`equipment_code`);

-- ============================================
-- 21. 设备维护保养记录 (equipment_maintenance)
-- ============================================
INSERT INTO `equipment_maintenance` (`cost_id`, `stat_mouth`, `total_cost`, `plan_count`, `manage_count`, `proceed_count`, `overdue_count`, `create_time`, `update_time`) VALUES
    (1, '2026-01', 12.50, 20, 18, 1, 1, NOW(), NOW()),
    (2, '2026-02', 8.30,  15, 15, 0, 0, NOW(), NOW()),
    (3, '2026-03', 15.00, 22, 20, 2, 0, NOW(), NOW()),
    (4, '2026-04', 10.20, 18, 17, 1, 0, NOW(), NOW()),
    (5, '2026-05', 9.80,  16, 14, 1, 1, NOW(), NOW()),
    (6, '2026-06', 11.50, 20, 19, 0, 1, NOW(), NOW()),
    (7, '2026-07', 13.00, 21, 20, 1, 0, NOW(), NOW()),
    (8, '2026-08', 7.60,  14, 13, 1, 0, NOW(), NOW()),
    (9, '2026-09', 6.00,  10, 5,  3, 2, NOW(), NOW())
    ON DUPLICATE KEY UPDATE `stat_mouth` = VALUES(`stat_mouth`);

-- ============================================
-- 22. 农机调配结果 (machinery_dispatch_result)
-- ============================================
INSERT INTO `machinery_dispatch_result` (`id`, `region_id`, `device_type`, `number`, `utilization_rate`, `create_time`, `update_time`) VALUES
    (1,  1, '收割设备', 12, 92.00, NOW(), NOW()),
    (2,  1, '耕作设备', 8,  88.00, NOW(), NOW()),
    (3,  1, '播种设备', 10, 95.00, NOW(), NOW()),
    (4,  1, '灌溉设备', 6,  85.00, NOW(), NOW()),
    (5,  1, '植保设备', 4,  90.00, NOW(), NOW()),
    (6,  2, '收割设备', 10, 90.00, NOW(), NOW()),
    (7,  2, '耕作设备', 7,  85.00, NOW(), NOW()),
    (8,  2, '播种设备', 8,  92.00, NOW(), NOW()),
    (9,  2, '灌溉设备', 5,  88.00, NOW(), NOW()),
    (10, 2, '植保设备', 3,  86.00, NOW(), NOW()),
    (11, 3, '收割设备', 8,  88.00, NOW(), NOW()),
    (12, 3, '耕作设备', 6,  82.00, NOW(), NOW()),
    (13, 3, '播种设备', 7,  90.00, NOW(), NOW()),
    (14, 3, '灌溉设备', 4,  84.00, NOW(), NOW()),
    (15, 3, '植保设备', 3,  89.00, NOW(), NOW()),
    (16, 4, '收割设备', 6,  91.00, NOW(), NOW()),
    (17, 4, '耕作设备', 5,  86.00, NOW(), NOW()),
    (18, 4, '播种设备', 5,  93.00, NOW(), NOW()),
    (19, 4, '灌溉设备', 3,  87.00, NOW(), NOW()),
    (20, 4, '植保设备', 2,  90.00, NOW(), NOW())
    ON DUPLICATE KEY UPDATE `device_type` = VALUES(`device_type`);

-- ============================================
-- 23. 设备调配结果存档 (equipment_allocation_results)
-- ============================================
INSERT INTO `equipment_allocation_results` (`id`, `task_id`, `request`, `result`, `created_at`) VALUES
    (1, 'eq-task-001', '{"task": "农机调配", "area": "全部片区"}', '{"pieData": [{"value": 50, "name": "收割设备"}], "barCategories": ["东区","西区","南区","北区"], "barData": [95, 90, 88, 82], "distanceReduction": 23.5, "efficiencyImprovement": 18.5}', NOW()),
    (2, 'eq-task-002', '{"task": "路径优化", "area": "东片区"}', '{"pieData": [{"value": 30, "name": "耕作设备"}], "barCategories": ["东区"], "barData": [92], "distanceReduction": 15.0, "efficiencyImprovement": 12.0}', NOW())
    ON DUPLICATE KEY UPDATE `task_id` = VALUES(`task_id`);

-- ============================================
-- 24. 通用任务表 (tasks)
-- ============================================
INSERT INTO `tasks` (`id`, `task_id`, `type`, `status`, `request_payload`, `result`, `progress`, `error_message`, `created_at`, `updated_at`, `finished_at`) VALUES
    (1, 'task-ai-001', 'ai_optimize', 'success', '{"type": "farmland", "year": 2026}', '{"status": "completed"}', 100.00, NULL, '2026-09-01 10:00:00', '2026-09-01 10:05:00', '2026-09-01 10:05:00'),
    (2, 'task-ai-002', 'ai_optimize', 'running', '{"type": "water", "year": 2026}', NULL, 65.00, NULL, '2026-09-07 08:00:00', '2026-09-07 08:02:00', NULL),
    (3, 'task-ai-003', 'ai_optimize', 'failed', '{"type": "seed", "year": 2026}', NULL, 30.00, 'AI API 调用超时', '2026-09-05 14:00:00', '2026-09-05 14:01:00', '2026-09-05 14:01:00')
    ON DUPLICATE KEY UPDATE `task_id` = VALUES(`task_id`);

-- ============================================
-- 25. 人工调配数据 (dispatch_compare_result)
-- ============================================
INSERT INTO `dispatch_compare_result` (`id`, `yield_benefit`, `risk_control`, `soil_protect`, `labor_util`, `material_efficiency`, `water_save`, `create_time`, `update_time`) VALUES
    (1, 75.00, 68.00, 72.00, 65.00, 70.00, 60.00, NOW(), NOW())
    ON DUPLICATE KEY UPDATE `id` = VALUES(`id`);

-- ============================================
-- 26. AI结果表 (ai_results)
-- ============================================
INSERT INTO `ai_results` (`id`, `task_id`, `request`, `result`, `created_at`) VALUES
    (1, 'ai-result-001', '{"type": "ai_decision", "period": "2026年度"}', '{"aiRadar": [92, 88, 85, 82, 90, 91], "convergenceRate": "98.6%", "yieldIncrease": 12.5}', NOW()),
    (2, 'ai-result-002', '{"type": "yield_predict", "year": "2026"}', '{"predicted": [520, 480, 350, 280]}', NOW())
    ON DUPLICATE KEY UPDATE `task_id` = VALUES(`task_id`);

-- ============================================
-- 27. 产量预测表 (predict_yield)
-- ============================================
INSERT INTO `predict_yield` (`id`, `stat_mouth`, `region_id`, `crop`, `predicted_value`, `created_at`) VALUES
    (1,  '2026-01', 1, '水稻', 120.00, NOW()),
    (2,  '2026-02', 1, '水稻', 115.00, NOW()),
    (3,  '2026-03', 1, '水稻', 130.00, NOW()),
    (4,  '2026-04', 1, '水稻', 140.00, NOW()),
    (5,  '2026-05', 1, '水稻', 155.00, NOW()),
    (6,  '2026-06', 1, '水稻', 160.00, NOW()),
    (7,  '2026-07', 1, '水稻', 150.00, NOW()),
    (8,  '2026-08', 1, '水稻', 145.00, NOW()),
    (9,  '2026-01', 2, '小麦', 100.00, NOW()),
    (10, '2026-02', 2, '小麦', 95.00,  NOW()),
    (11, '2026-03', 2, '小麦', 110.00, NOW()),
    (12, '2026-04', 2, '小麦', 120.00, NOW()),
    (13, '2026-05', 2, '小麦', 130.00, NOW()),
    (14, '2026-06', 2, '小麦', 135.00, NOW()),
    (15, '2026-07', 2, '小麦', 125.00, NOW()),
    (16, '2026-08', 2, '小麦', 120.00, NOW()),
    (17, '2026-01', 3, '玉米', 90.00,  NOW()),
    (18, '2026-02', 3, '玉米', 85.00,  NOW()),
    (19, '2026-03', 3, '玉米', 100.00, NOW()),
    (20, '2026-04', 3, '玉米', 110.00, NOW()),
    (21, '2026-05', 3, '玉米', 115.00, NOW()),
    (22, '2026-06', 3, '玉米', 120.00, NOW()),
    (23, '2026-07', 3, '玉米', 118.00, NOW()),
    (24, '2026-08', 3, '玉米', 112.00, NOW()),
    (25, '2026-01', 4, '大豆', 60.00,  NOW()),
    (26, '2026-02', 4, '大豆', 58.00,  NOW()),
    (27, '2026-03', 4, '大豆', 65.00,  NOW()),
    (28, '2026-04', 4, '大豆', 70.00,  NOW()),
    (29, '2026-05', 4, '大豆', 75.00,  NOW()),
    (30, '2026-06', 4, '大豆', 78.00,  NOW()),
    (31, '2026-07', 4, '大豆', 72.00,  NOW()),
    (32, '2026-08', 4, '大豆', 70.00,  NOW())
    ON DUPLICATE KEY UPDATE `crop` = VALUES(`crop`);

-- ============================================
-- 28. 资源需求预测表 (predict_resource)
-- ============================================
INSERT INTO `predict_resource` (`id`, `stat_mouth`, `region_id`, `water_demand`, `fertilizer_demand`, `labor_demand`, `created_at`) VALUES
    (1,  '2026-01', 1, 180.00, 50.00, 25,  NOW()),
    (2,  '2026-02', 1, 190.00, 55.00, 28,  NOW()),
    (3,  '2026-03', 1, 220.00, 65.00, 35,  NOW()),
    (4,  '2026-04', 1, 250.00, 70.00, 38,  NOW()),
    (5,  '2026-05', 1, 270.00, 75.00, 40,  NOW()),
    (6,  '2026-06', 1, 260.00, 72.00, 35,  NOW()),
    (7,  '2026-07', 1, 240.00, 68.00, 30,  NOW()),
    (8,  '2026-08', 1, 230.00, 65.00, 28,  NOW()),
    (9,  '2026-01', 2, 150.00, 45.00, 20,  NOW()),
    (10, '2026-02', 2, 160.00, 48.00, 22,  NOW()),
    (11, '2026-03', 2, 180.00, 55.00, 28,  NOW()),
    (12, '2026-04', 2, 200.00, 60.00, 30,  NOW()),
    (13, '2026-05', 2, 210.00, 62.00, 32,  NOW()),
    (14, '2026-06', 2, 200.00, 58.00, 28,  NOW()),
    (15, '2026-07', 2, 190.00, 55.00, 25,  NOW()),
    (16, '2026-08', 2, 180.00, 52.00, 22,  NOW()),
    (17, '2026-01', 3, 140.00, 40.00, 18,  NOW()),
    (18, '2026-02', 3, 145.00, 42.00, 19,  NOW()),
    (19, '2026-03', 3, 160.00, 48.00, 22,  NOW()),
    (20, '2026-04', 3, 175.00, 52.00, 25,  NOW()),
    (21, '2026-05', 3, 185.00, 55.00, 28,  NOW()),
    (22, '2026-06', 3, 180.00, 52.00, 25,  NOW()),
    (23, '2026-07', 3, 170.00, 50.00, 22,  NOW()),
    (24, '2026-08', 3, 165.00, 48.00, 20,  NOW()),
    (25, '2026-01', 4, 120.00, 35.00, 15,  NOW()),
    (26, '2026-02', 4, 125.00, 36.00, 16,  NOW()),
    (27, '2026-03', 4, 135.00, 40.00, 18,  NOW()),
    (28, '2026-04', 4, 145.00, 42.00, 20,  NOW()),
    (29, '2026-05', 4, 150.00, 44.00, 22,  NOW()),
    (30, '2026-06', 4, 148.00, 43.00, 20,  NOW()),
    (31, '2026-07', 4, 140.00, 40.00, 18,  NOW()),
    (32, '2026-08', 4, 135.00, 38.00, 16,  NOW())
    ON DUPLICATE KEY UPDATE `water_demand` = VALUES(`water_demand`);

-- ============================================
-- 29. 资源调配总览 (resource_overview)
-- ============================================
INSERT INTO `resource_overview` (`id`, `stat_mouth`, `farmland`, `water`, `material`, `labor`, `machine`) VALUES
    (1,  '2026-01', 4200.00, 6500.00, 4800.00, 850.00,  120.00),
    (2,  '2026-02', 4250.00, 6200.00, 4600.00, 820.00,  115.00),
    (3,  '2026-03', 4300.00, 7000.00, 5200.00, 950.00,  130.00),
    (4,  '2026-04', 4400.00, 7500.00, 5500.00, 1020.00, 135.00),
    (5,  '2026-05', 4450.00, 7800.00, 5800.00, 1100.00, 140.00),
    (6,  '2026-06', 4500.00, 8000.00, 6000.00, 1150.00, 145.00),
    (7,  '2026-07', 4480.00, 7600.00, 5600.00, 1050.00, 138.00),
    (8,  '2026-08', 4500.00, 7200.00, 5300.00, 980.00,  132.00),
    (9,  '2026-09', 4500.00, 7000.00, 5100.00, 920.00,  128.00)
    ON DUPLICATE KEY UPDATE `stat_mouth` = VALUES(`stat_mouth`);

-- ============================================
-- 30. 资源利用率趋势 (resource_util_trend)
-- ============================================
INSERT INTO `resource_util_trend` (`id`, `stat_mouth`, `land_util_rate`, `water_util_rate`, `material_util_rate`, `labor_util_rate`, `created_at`) VALUES
    (1, '2026-01', 68.50, 65.00, 72.00, 78.00, NOW()),
    (2, '2026-02', 67.80, 62.00, 70.50, 76.00, NOW()),
    (3, '2026-03', 72.00, 70.00, 75.00, 82.00, NOW()),
    (4, '2026-04', 74.50, 75.00, 78.00, 85.00, NOW()),
    (5, '2026-05', 76.00, 78.00, 80.00, 88.00, NOW()),
    (6, '2026-06', 78.00, 80.00, 82.00, 90.00, NOW()),
    (7, '2026-07', 75.50, 76.00, 79.00, 86.00, NOW()),
    (8, '2026-08', 73.00, 72.00, 76.00, 84.00, NOW()),
    (9, '2026-09', 71.00, 70.00, 74.00, 82.00, NOW())
    ON DUPLICATE KEY UPDATE `stat_mouth` = VALUES(`stat_mouth`);

-- ============================================
-- 31. 资源调配异常预警 (resource_alert)
-- ============================================
INSERT INTO `resource_alert` (`id`, `alert_type`, `region_name`, `target_name`, `alert_level`, `alert_content`, `suggest_action`, `is_handle`, `create_time`, `handle_time`) VALUES
    (1,  '用水预警',   '南片区', '用水配额', 'danger',  '南片区用水配额使用率达86.36%，已超过警戒线85%，存在严重缺水风险。', '建议立即启动节水应急预案，调配邻近片区水资源，并开启AI智能灌溉模式。', 0, NOW(), NULL),
    (2,  '库存预警',   '西片区', '玉米种子', 'warning', '玉米种子库存仅剩600吨，低于安全阈值200吨，需尽快补货。', '建议从东片区调拨玉米种子，或启动紧急采购流程。', 0, NOW(), NULL),
    (3,  '库存预警',   '东片区', '杀虫剂',  'warning', '杀虫剂库存仅剩800吨，接近安全阈值200吨，预计下月将出现缺口。', '建议提前采购杀虫剂，保障下月农事需求。', 0, NOW(), NULL),
    (4,  '农机维护',   '南片区', '播种机F型', 'info', '播种机F型当前状态为"维修中"，设备评分65分，已低于正常水平。', '建议尽快完成维修，并安排备用设备顶替。', 0, NOW(), NULL),
    (5,  '农机维护',   '北片区', '耕作机H型', 'warning', '耕作机H型当前状态为"亚健康"，设备评分72分，存在故障风险。', '建议安排预防性维护保养，避免影响秋收作业。', 0, NOW(), NULL),
    (6,  '用水预警',   '东片区', '用水配额', 'info', '东片区用水配额使用率73.33%，处于正常范围，运行良好。', '保持当前用水方案，继续监控。', 1, '2026-09-01 09:00:00', '2026-09-01 12:00:00'),
    (7,  '方案执行',   '全部片区', '秋收准备工作', 'info', '秋收季节即将到来，建议各片区提前做好秋收准备工作。', '1.检查收割设备状态 2.调配劳动力 3.准备仓储设施', 0, NOW(), NULL),
    (8,  '库存预警',   '东片区', '复合肥', 'info', '复合肥库存充足，当前5000吨，安全阈值1000吨，无需补货。', '继续保持当前库存水平。', 1, '2026-09-02 08:00:00', '2026-09-02 10:00:00'),
    (9,  '方案执行',   '全部片区', '冬藏整地', 'info', '冬季即将来临，建议启动冬藏整地准备工作。', '1.安排整地劳动力 2.调配耕作设备 3.制定休耕计划', 0, NOW(), NULL),
    (10, '用水预警',   '北片区', '用水配额', 'success', '北片区用水配额使用率仅56.52%，水资源利用效率优秀。', '继续保持高效用水方案，可作为其他片区参考标杆。', 1, '2026-09-03 10:00:00', '2026-09-03 14:00:00')
    ON DUPLICATE KEY UPDATE `alert_content` = VALUES(`alert_content`);

-- ============================================
-- 完成
-- ============================================
SELECT '测试数据插入完成！' AS message;
SELECT COUNT(*) AS region_count FROM regions;
SELECT COUNT(*) AS user_count FROM user_data;
SELECT COUNT(*) AS farmland_count FROM farmland_blocks;
SELECT COUNT(*) AS water_quota_count FROM water_quota;
SELECT COUNT(*) AS seed_count FROM seed_inventory;
SELECT COUNT(*) AS labor_count FROM labor_workers;
SELECT COUNT(*) AS equipment_count FROM equipment;
SELECT COUNT(*) AS alert_count FROM resource_alert;