package com.rx.webapi.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcBeanPostProcessor implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
            adapter.setSynchronizeOnSession(true);
            log.info("Enable SynchronizeOnSession on RequestMappingHandlerAdapter");
        }
        if (bean instanceof UrlBasedViewResolver) {
            UrlBasedViewResolver urlBasedViewResolver = (UrlBasedViewResolver) bean;
            urlBasedViewResolver.setRedirectHttp10Compatible(false);
            log.info("Disable RedirectHttp10Compatible on {}", bean.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

}