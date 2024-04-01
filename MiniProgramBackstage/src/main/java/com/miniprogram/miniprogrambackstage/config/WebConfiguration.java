package com.miniprogram.miniprogrambackstage.config;

import com.miniprogram.miniprogrambackstage.interceptor.GlobalInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有路由映射
//        registry.addInterceptor(new GlobalInterceptor()).addPathPatterns("/**").excludePathPatterns("/login","/getfile/**");
    }
}
