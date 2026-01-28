package org.example.framework.aop.interceptor;


import org.example.framework.aop.annotation.Retry;

import java.lang.reflect.Method;

public class RetryInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Method targetMethod = invocation.getTarget()
                .getClass()
                .getMethod(method.getName(), method.getParameterTypes());

        Retry retry = targetMethod.getAnnotation(Retry.class);

        if (retry == null)
            return invocation.proceed();

        int maxAttempts = retry.maxAttempts();
        long delayMs = retry.delayMs();
        Class<? extends Throwable>[] retryOn = retry.retryOn();

        Throwable last = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return invocation.copy().proceed();
            } catch (Throwable ex) {
                last = ex;

                if (!isRetryTarget(ex, retryOn))
                    break;

                if (attempt < maxAttempts && delayMs > 0) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }
            }
        }

        throw last;
    }

    private boolean isRetryTarget(
            Throwable ex,
            Class<? extends Throwable>[] retryOn
    ) {
        for (Class<? extends Throwable> type : retryOn) {
            if (type.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }
}
