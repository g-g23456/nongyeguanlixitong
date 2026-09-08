package com.panduoma.demo.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 接口路径与权限码映射
 * 定义每个 API 路径所需的权限点编码
 * 不在映射表中的 /api/** 路径仅需登录，无需特定权限
 */
public class PermissionPaths {

    /** 路径 → 所需权限码 */
    private static final Map<String, String> PATH_PERMISSION_MAP;

    /** 无需任何认证即可访问的路径（登录、API文档） */
    private static final Set<String> EXCLUDE_PATHS;

    static {
        Map<String, String> map = new HashMap<>();

        // ===== 看板 =====
        map.put("/api/dashboard/overview", "dashboard:view");

        // ===== 耕地管理 =====
        map.put("/api/farmland/list", "farmland:view");
        map.put("/api/farmland/create", "farmland:create");
        map.put("/api/farmland/optimize", "farmland:optimize");
        map.put("/api/farmland/optimize/latest", "farmland:optimize");
        map.put("/api/farmland/optimize/blocks", "farmland:optimize");
        map.put("/api/farmland/rotation", "farmland:rotation");

        // ===== 用水管理 =====
        map.put("/api/water/status", "water:view");
        map.put("/api/water/quota", "water:quota:view");
        map.put("/api/water/quota/update", "water:quota:update");
        map.put("/api/water/allocation", "water:allocate");
        map.put("/api/water/analysis", "water:analysis");

        // ===== 种子/农资管理 =====
        map.put("/api/seed/inventory", "seed:view");
        map.put("/api/seed/create", "seed:create");
        map.put("/api/seed/allocation", "seed:allocate");
        map.put("/api/seed/predict", "seed:predict");

        // ===== 劳动力管理 =====
        map.put("/api/labor/list", "labor:view");
        map.put("/api/labor/create", "labor:create");
        map.put("/api/labor/old/schedule", "labor:schedule:view");
        map.put("/api/labor/schedule", "labor:schedule:manage");

        // ===== 设备管理 =====
        map.put("/api/equipment/list", "equipment:view");
        map.put("/api/equipment/create", "equipment:create");
        map.put("/api/equipment/status", "equipment:status");
        map.put("/api/equipment/allocation", "equipment:allocate");
        map.put("/api/equipment/maintenance", "equipment:maintain");

        // ===== AI 与预测 =====
        map.put("/api/ai/decision", "ai:decision");
        map.put("/api/predict/yield", "predict:yield");
        map.put("/api/predict/resource", "predict:resource");

        // ===== 系统管理（仅 admin） =====
        map.put("/api/system/users", "system:users");
        map.put("/api/system/create", "system:create");

        PATH_PERMISSION_MAP = Collections.unmodifiableMap(map);

        Set<String> excludes = new HashSet<>();
        excludes.add("/api/auth/login");
        excludes.add("/swagger-ui.html");
        excludes.add("/swagger-ui/**");
        excludes.add("/v3/api-docs/**");
        excludes.add("/doc.html");
        excludes.add("/webjars/**");
        excludes.add("/favicon.ico");
        EXCLUDE_PATHS = Collections.unmodifiableSet(excludes);
    }

    /**
     * 获取指定路径所需的权限码
     * @return 权限码，null 表示仅需登录无需特定权限
     */
    public static String getRequiredPermission(String path) {
        return PATH_PERMISSION_MAP.get(path);
    }

    /**
     * 判断路径是否需要权限校验
     */
    public static boolean requiresPermission(String path) {
        return PATH_PERMISSION_MAP.containsKey(path);
    }

    /**
     * 判断路径是否排除认证（登录、文档等）
     */
    public static boolean isExcluded(String path) {
        if (EXCLUDE_PATHS.contains(path)) {
            return true;
        }
        // 支持通配符匹配
        for (String pattern : EXCLUDE_PATHS) {
            if (pattern.endsWith("/**")) {
                String prefix = pattern.substring(0, pattern.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }
}