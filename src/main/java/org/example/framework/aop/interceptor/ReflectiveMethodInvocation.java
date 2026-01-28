package org.example.framework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation {

    private final Object target;
    private final Method method;
    private final Object[] args;
    private final List<MethodInterceptor> interceptors;
    private int index = -1;

    public ReflectiveMethodInvocation(
            Object target,
            Method method,
            Object[] args,
            List<MethodInterceptor> interceptors
    ) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.interceptors = interceptors;
    }

    @Override
    public Object proceed() throws Throwable {
        index++;
        if (index == interceptors.size()) {
            return method.invoke(target, args);
        }
        return interceptors.get(index).invoke(this);
    }

    @Override public Object getTarget() { return target; }

    @Override public Method getMethod() { return method; }
    @Override public Object[] getArguments() { return args; }

    @Override
    public MethodInvocation copy() {
        ReflectiveMethodInvocation copy = new ReflectiveMethodInvocation(target, method, args, interceptors);
        copy.index = this.index;
        return copy;
    }
}