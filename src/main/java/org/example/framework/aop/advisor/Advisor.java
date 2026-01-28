package org.example.framework.aop.advisor;

import org.example.framework.aop.interceptor.MethodInterceptor;
import org.example.framework.aop.pointcut.Pointcut;

import java.lang.reflect.Method;

public class Advisor {

    private final Pointcut pointcut;
    private final MethodInterceptor interceptor;

    public Advisor(Pointcut pointcut, MethodInterceptor interceptor) {
        this.pointcut = pointcut;
        this.interceptor = interceptor;
    }

    public boolean matches(Method method, Class<?> targetClass) {
        return pointcut.matches(method, targetClass);
    }

    public MethodInterceptor getInterceptor() {
        return interceptor;
    }
}