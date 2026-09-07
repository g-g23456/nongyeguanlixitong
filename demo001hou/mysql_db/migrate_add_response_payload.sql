ALTER TABLE `farmland_optimize_results`
    ADD COLUMN `response_payload` JSON DEFAULT NULL COMMENT 'AI优化结果'
    AFTER `request_payload`;