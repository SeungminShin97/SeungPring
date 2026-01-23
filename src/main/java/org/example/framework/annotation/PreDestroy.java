package org.example.framework.annotation;

import java.lang.annotation.*;

/**
 * Bean 소멸 직전 호출되는 라이프사이클 콜백 어노테이션.
 *
 * - singleton Bean에만 적용
 * - Context close 시 호출된다
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
}
