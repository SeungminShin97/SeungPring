package org.example.framework.aop.profile;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileRepository {

    private static final Map<Method, MethodProfile> STORE = new ConcurrentHashMap<>();

    public static MethodProfile get(Method method) {
        return STORE.computeIfAbsent(method, m -> new MethodProfile());
    }

    public static Map<Method, MethodProfile> snapshot() {
        return Map.copyOf(STORE);
    }
}