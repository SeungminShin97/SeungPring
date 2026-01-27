package org.example.app.config;

import org.example.framework.annotation.Component;
import org.example.framework.web.config.WebMvcConfigurer;
import org.example.framework.web.interceptor.InterceptorRegistry;
import org.example.app.interceptor.LoggingInterceptor;

@Component
public class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }
}
