package org.example.framework.aop.interceptor;

public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}