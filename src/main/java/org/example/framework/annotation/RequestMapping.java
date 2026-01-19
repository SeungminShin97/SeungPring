package org.example.framework.annotation;

import org.example.framework.was.protocol.model.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    /**
     * 요청 경로
     */
    String value();

    /**
     * Http 메서드
     */
    HttpMethod method() default HttpMethod.GET;
}
