package org.example.framework.aop.interceptor;

import java.lang.reflect.Method;

public interface MethodInvocation {

    Object getTarget();
    Method getMethod();
    Object[] getArguments();

    Object proceed() throws Throwable;

    MethodInvocation copy();
}