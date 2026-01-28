package org.example.framework.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    int maxAttempts() default 3;

    long delayMs() default 0;

    Class<? extends Throwable>[] retryOn() default { Exception.class };
}