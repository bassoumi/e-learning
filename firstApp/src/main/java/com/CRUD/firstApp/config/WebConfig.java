package com.CRUD.firstApp.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1) Mapping pour les images des instructeurs (/uploads/images/**)
        String uploadImagesPath = Paths
                .get("uploads/images")
                .toAbsolutePath()
                .toUri()
                .toString();
        registry
                .addResourceHandler("/uploads/images/**")
                .addResourceLocations(uploadImagesPath);

        // 2) Mapping pour les fichiers de cours (/uploads/courses/**)
        String uploadCoursesPath = Paths
                .get("uploads/courses")
                .toAbsolutePath()
                .toUri()
                .toString();
        registry
                .addResourceHandler("/uploads/courses/**")
                .addResourceLocations(uploadCoursesPath);
    }
}
