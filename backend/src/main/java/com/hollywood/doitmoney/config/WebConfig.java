package com.hollywood.doitmoney.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/profiles}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = uploadPath.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        // "/static/profiles/**" 요청은 전부 파일시스템 uploads/profiles/ 아래에서 찾음
        registry
                .addResourceHandler("/static/profiles/**")
                .addResourceLocations(location);
    }
}

// git add .
// git commit -m "feat: JWT 구현 완료 및 초기 세팅 완료"