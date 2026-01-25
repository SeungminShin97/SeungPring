package org.example.framework.annotation;


import org.example.framework.context.ScopeType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    /**
     * 빈 이름
     * 비어 있으면 메서드 이름 사용
     */
    String name() default "";

    /**
     * 스코프
     */
    ScopeType scope() default ScopeType.SINGLETON;

    /**
     * lazy 여부
     */
    boolean lazy() default false;
}
