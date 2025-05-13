package com.example.mhbc.config;

import com.fasterxml.jackson.core.Base64Variant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/data/**") // URL 경로
                .addResourceLocations("file:C:/Users/YJ/Documents/GitHub/public/data/"); // 실제 폴더 경로(192.168.0.190)
                /*.addResourceLocations("file:Z:/public/data/");//로컬*/
    }
}
