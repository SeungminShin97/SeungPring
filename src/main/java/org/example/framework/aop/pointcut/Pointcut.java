package org.example.framework.aop.pointcut;

import java.lang.reflect.Method;

public interface Pointcut {
    boolean matches(Method method, Class<?> targetClass);
}