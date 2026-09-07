package com.panduoma.demo.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.panduoma.demo.filter.RequestBodyCacheFilter;
import com.panduoma.demo.interceptor.PermissionInterceptor;
import jakarta.annotation.Resource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Resource
    private PermissionInterceptor permissionInterceptor;

    @Bean
    public FilterRegistrationBean<RequestBodyCacheFilter> requestBodyCacheFilter() {
        FilterRegistrationBean<RequestBodyCacheFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestBodyCacheFilter());
        registration.addUrlPatterns("/*");
        registration.setName("requestBodyCacheFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 权限拦截器：从 body 中的 satoken 做登录 + 权限校验
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui.html", "/swagger-ui/**",
                        "/v3/api-docs/**", "/doc.html", "/webjars/**"
                );
    }
}
