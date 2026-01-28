package org.example.framework.aop.pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationPointcut implements Pointcut {

    private final Class<? extends Annotation> annotationType;

    public AnnotationPointcut(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (method.isAnnotationPresent(annotationType)) return true;

        try {
            Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.isAnnotationPresent(annotationType);
        } catch (NoSuchMethodException e ) {
            return false;
        }
    }
}
