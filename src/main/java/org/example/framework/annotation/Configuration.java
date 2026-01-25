package org.example.framework.annotation;

import java.lang.annotation.*;

/**
 * @Bean 메서드를 포함하는 설정 클래스임을 나타내는 어노테이션
 *
 * <p>
 * 해당 클래스는 하나의 빈으로 등록되며,
 * 내부 @Bean 메서드들은 추가적인 빈 정의로 처리된다.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
}
