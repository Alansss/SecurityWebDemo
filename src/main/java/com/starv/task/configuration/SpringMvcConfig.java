package com.starv.task.configuration;

import com.starv.task.configuration.resolver.JSONResolver;
import com.starv.task.configuration.security.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    private final SecurityInterceptor starVSecurityInterceptor;

    private final JSONResolver jsonResolver;

    @Autowired
    public SpringMvcConfig(SecurityInterceptor starVSecurityInterceptor, JSONResolver jsonResolver) {
        this.starVSecurityInterceptor = starVSecurityInterceptor;
        this.jsonResolver = jsonResolver;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(starVSecurityInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(jsonResolver);
    }

}