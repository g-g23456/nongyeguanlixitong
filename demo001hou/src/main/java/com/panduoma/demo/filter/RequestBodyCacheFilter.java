package com.panduoma.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 请求体缓存过滤器
 * 1. 将请求体缓存到 CachedBodyHttpServletRequest 中，使后续 Interceptor 和 Controller 都能读取
 * 2. 从 JSON 请求体中提取 satoken 字段，存入请求属性供权限拦截器使用
 */
public class RequestBodyCacheFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 仅处理 POST 请求（本项目所有业务接口均为 POST）
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 缓存请求体
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        byte[] body = cachedRequest.getCachedBody();

        // 尝试从 JSON 请求体中提取 satoken
        if (body != null && body.length > 0) {
            String bodyStr = new String(body, java.nio.charset.StandardCharsets.UTF_8);
            String token = extractSatokenFromJson(bodyStr);
            if (token != null && !token.isBlank()) {
                cachedRequest.setAttribute("satoken_from_body", token);
            }
        }

        // 如果请求体中没有 satoken，尝试从 Authorization Header 中提取
        if (cachedRequest.getAttribute("satoken_from_body") == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                cachedRequest.setAttribute("satoken_from_body", authHeader.substring(7));
            }
        }

        filterChain.doFilter(cachedRequest, response);
    }

    /**
     * 简易 JSON 字段提取（避免引入额外 JSON 库依赖）
     * 从 JSON 字符串中提取 "satoken" 字段的值
     */
    private String extractSatokenFromJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        // 匹配 "satoken" : "value" 或 "satoken":"value"
        String key = "\"satoken\"";
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) {
            return null;
        }

        // 找到 key 后面的冒号
        int colonIndex = json.indexOf(':', keyIndex + key.length());
        if (colonIndex == -1) {
            return null;
        }

        // 跳过冒号后的空白字符
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            return null;
        }

        // 判断值是字符串还是 null
        if (json.charAt(valueStart) == 'n') {
            return null; // null value
        }

        // 值必须是双引号包裹的字符串
        if (json.charAt(valueStart) != '"') {
            return null;
        }

        // 找到结束引号（处理转义字符）
        int valueEnd = valueStart + 1;
        while (valueEnd < json.length()) {
            if (json.charAt(valueEnd) == '\\') {
                valueEnd += 2; // 跳过转义字符
                continue;
            }
            if (json.charAt(valueEnd) == '"') {
                break;
            }
            valueEnd++;
        }

        if (valueEnd >= json.length()) {
            return null;
        }

        return json.substring(valueStart + 1, valueEnd);
    }
}