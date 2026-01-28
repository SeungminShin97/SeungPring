package org.example.framework.aop.interceptor;

import org.example.framework.aop.annotation.Profiled;
import org.example.framework.aop.profile.MethodProfile;
import org.example.framework.aop.profile.ProfileRepository;

import java.lang.reflect.Method;

public class ProfiledInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Method targetMethod = invocation.getTarget().getClass()
                .getMethod(method.getName(), method.getParameterTypes());
        Profiled profiled = targetMethod.getAnnotation(Profiled.class);

        if (profiled == null)
            return invocation.proceed();

        long start = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            long elapsed = System.nanoTime() - start;
            MethodProfile profile = ProfileRepository.get(method);
            profile.record(elapsed);
        }
    }
}