package com.basic.myspringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mobile/**")
                //반드시 mobile 다음에 / 을 주어야 한다.
                .addResourceLocations("classpath:/mobile/")
                .setCachePeriod(20);//20초
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // 특정 도메인 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}