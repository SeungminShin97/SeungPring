package org.example.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean 생성 및 의존성 주입 완료 후
 * 초기화 로직을 수행하기 위한 라이프사이클 콜백 어노테이션.
 *
 * - 파라미터 없는 메서드에만 적용 가능
 * - BeanPostProcessor 단계에서 호출된다
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
