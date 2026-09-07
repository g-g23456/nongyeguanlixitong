package com.panduoma.demo.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panduoma.demo.config.PermissionPaths;
import com.panduoma.demo.filter.CachedBodyHttpServletRequest;
import com.panduoma.demo.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限拦截器
 * 1. 从请求体中提取 satoken 字段完成登录认证
 * 2. 根据请求路径查询所需权限码
 * 3. 通过 user_permissions + permissions 关联表校验用户权限
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();

        // 1. 排除路径（登录接口、API文档等）直接放行
        if (PermissionPaths.isExcluded(path)) {
            return true;
        }

        // 2. 非 /api/ 路径直接放行（静态资源等）
        if (!path.startsWith("/api/")) {
            return true;
        }

        // 3. 从请求属性、请求体或 Authorization Header 中获取 satoken
        String token = (String) request.getAttribute("satoken_from_body");
        if (token == null || token.isBlank()) {
            token = extractTokenFromBody(request);
        }
        if (token == null || token.isBlank()) {
            token = extractTokenFromHeader(request);
        }

        // 4. 无 token → 401
        if (token == null || token.isBlank()) {
            writeErrorResponse(response, ResponseCode.UNAUTHORIZED, "未登录，请先登录");
            return false;
        }

        // 5. 通过 token 获取登录用户 ID
        Object loginId;
        try {
            loginId = StpUtil.getLoginIdByToken(token);
        } catch (Exception e) {
            writeErrorResponse(response, ResponseCode.UNAUTHORIZED, "Token 无效或已过期");
            return false;
        }

        if (loginId == null) {
            writeErrorResponse(response, ResponseCode.UNAUTHORIZED, "Token 无效或已过期");
            return false;
        }

        // 6. 将当前请求的登录状态设置到 sa-token 上下文，使 Controller 中 StpUtil.getLoginIdAsLong() 可用
        StpUtil.setTokenValue(token);

        // 7. 检查该路径是否需要特定权限
        if (PermissionPaths.requiresPermission(path)) {
            String requiredPermission = PermissionPaths.getRequiredPermission(path);
            try {
                StpUtil.checkPermission(requiredPermission);
            } catch (Exception e) {
                writeErrorResponse(response, ResponseCode.FORBIDDEN,
                        "无权限访问，需要权限: " + requiredPermission);
                return false;
            }
        }

        return true;
    }

    /**
     * 从缓存的请求体中提取 satoken 字段
     */
    private String extractTokenFromBody(HttpServletRequest request) {
        if (!(request instanceof CachedBodyHttpServletRequest cachedRequest)) {
            return null;
        }
        byte[] body = cachedRequest.getCachedBody();
        if (body == null || body.length == 0) {
            return null;
        }
        try {
            String bodyStr = new String(body, java.nio.charset.StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(bodyStr, Map.class);
            Object satoken = map.get("satoken");
            return satoken != null ? satoken.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 Authorization Header 中提取 Bearer token
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 写入错误响应（JSON 格式）
     */
    private void writeErrorResponse(HttpServletResponse response, ResponseCode code, String message) throws IOException {
        response.setStatus(code.getCode());
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", code.getCode());
        result.put("message", message);
        result.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}