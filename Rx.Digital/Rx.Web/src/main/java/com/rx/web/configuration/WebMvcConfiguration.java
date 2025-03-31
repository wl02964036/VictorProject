package com.rx.web.configuration;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rx.web.interceptor.CaptchaInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Value("${app.upload.path}")
	protected String uploadRootPath;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		CaptchaInterceptor captchaInterceptor = new CaptchaInterceptor(uploadRootPath);
		registry.addInterceptor(captchaInterceptor).addPathPatterns("/login");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String wavPath = uploadRootPath + File.separator + "wav" + File.separator;
		File wavPathF = new File(wavPath);
		if (!wavPathF.exists()) {
			wavPathF.mkdir();
		}

		registry.addResourceHandler("/wav/**").addResourceLocations("file:" + wavPath);
	}

}
