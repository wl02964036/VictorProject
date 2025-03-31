package com.rx.webapi.configuration;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Value("${app.upload.path}")
	protected String uploadRootPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String wavPath = String.format("%s%s%s%s", uploadRootPath, File.separator, "wav", File.separator);
		File wavPathF = new File(wavPath);
		if (!wavPathF.exists()) {
			wavPathF.mkdir();
		}

		registry.addResourceHandler("/angular/wav/**").addResourceLocations("file:" + wavPath);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
	}

}