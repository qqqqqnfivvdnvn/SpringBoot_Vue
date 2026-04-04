package com.example.demo.config;

import com.example.demo.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/demo/hello/**")
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/**/*.js",
                        "/**/*.css",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/api/**"
                );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 首页
        registry.addViewController("/").setViewName("forward:/index.html");

        // Vue History 路由支持
        registry.addViewController("/{spring:[\\w-]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:[\\w-]+}")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 添加过滤器，将 /api/* 请求重写为 /*
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> apiRewriteFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain filterChain)
                    throws ServletException, IOException {
                String path = request.getRequestURI();
                if (path != null && path.startsWith("/api/")) {
                    // 将 /api/xxx 重写为 /xxx
                    request.getRequestDispatcher(path.substring(4))
                            .forward(request, response);
                    return;
                }
                filterChain.doFilter(request, response);
            }
        });
        registration.addUrlPatterns("/api/*");
        registration.setName("apiRewriteFilter");
        registration.setOrder(1);
        return registration;
    }
}
