package org.example.framework.aop.proxy;

import org.example.framework.aop.advisor.Advisor;
import org.example.framework.aop.interceptor.MethodInterceptor;
import org.example.framework.aop.interceptor.MethodInvocation;
import org.example.framework.aop.interceptor.ReflectiveMethodInvocation;

import java.lang.reflect.Proxy;
import java.util.List;

public class AopProxyFactory {

    public static Object createProxy(Object target, List<Advisor> advisors) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalStateException("AOP proxy requires interface");
        }

        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                (proxy, method, args) -> {

                    List<MethodInterceptor> advisorsForMethod = advisors.stream()
                            .filter(a -> a.matches(method, target.getClass()))
                            .map(Advisor::getInterceptor)
                            .toList();

                    MethodInvocation invocation =
                            new ReflectiveMethodInvocation(target, method, args, advisorsForMethod);

                    return invocation.proceed();
                }
        );
    }
}