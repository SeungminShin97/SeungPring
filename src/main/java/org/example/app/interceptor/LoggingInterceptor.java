package org.example.app.interceptor;

import org.example.framework.annotation.Component;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.web.interceptor.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
//        System.out.println("[PRE] " + request.getMethod() + " " + request.getPath());
        return true;
    }

    @Override
    public void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("[AFTER] status=" + response.getStatus());
    }
}
