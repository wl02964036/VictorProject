package com.rx.webapi.configuration;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Value("${app.upload.path}")
	protected String uploadRootPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // wav 檔案處理
		String wavPath = String.format("%s%s%s%s", uploadRootPath, File.separator, "wav", File.separator);
		File wavPathF = new File(wavPath);
		if (!wavPathF.exists()) {
			wavPathF.mkdir();
		}

		registry.addResourceHandler("/angular/wav/**").addResourceLocations("file:" + wavPath);
		
        // Angular 靜態資源 fallback 處理
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    Resource requestedResource = location.createRelative(resourcePath);
                    return (requestedResource.exists() && requestedResource.isReadable())
                        ? requestedResource
                        : new ClassPathResource("/static/index.html"); // fallback 給 Angular 前端
                }
            });
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
	}

}