package com.panduoma.demo.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()

                // 指定 拦截路由 与 放行路由
                .addInclude("/**")
                .addExclude("/favicon.ico")
                .addExclude("/api/auth/login")
                .addExclude("/swagger-ui.html")
                .addExclude("/swagger-ui/**")
                .addExclude("/v3/api-docs/**")
                .addExclude("/doc.html")
                .addExclude("/webjars/**")

                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    System.out.println("---------- 进入Sa-Token全局认证 -----------");

                    // 登录认证 -- 拦截所有路由，排除登录接口和 API 文档路径
                    SaRouter.match("/**")
                            .notMatch("/api/auth/login",
                                    "/swagger-ui.html", "/swagger-ui/**",
                                    "/v3/api-docs/**", "/doc.html", "/webjars/**")
                            .check(r -> StpUtil.checkLogin());
                })

                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    System.out.println("---------- 进入Sa-Token异常处理 -----------");
                    SaHolder.getResponse().setStatus(401);
                    return SaResult.error(e.getMessage());
                })

                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("sa-server")
                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用，并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                    ;
                })
                ;
    }
    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui.html", "/swagger-ui/**",
                        "/v3/api-docs/**", "/doc.html", "/webjars/**"
                );
    }
}
