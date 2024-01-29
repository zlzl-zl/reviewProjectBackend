package com.hmdp.config;

import com.hmdp.intercept.LoginIntercept;
import com.hmdp.intercept.RefreshIntercept;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author: zl
 * @Date: 2023-12-31 0:02
 */
@SpringBootConfiguration
public class InterceptConfig implements WebMvcConfigurer {
    @Resource
    LoginIntercept loginIntercept;
    @Resource
    RefreshIntercept refreshIntercept;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(refreshIntercept).addPathPatterns("/**").order(0);
        registry.addInterceptor(loginIntercept).excludePathPatterns(
                "/user/login",
                "/user/code",
                "/user/logout"
        ).order(1);
    }
}
