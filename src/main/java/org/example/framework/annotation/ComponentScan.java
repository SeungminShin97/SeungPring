package org.example.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {

    /**
     * 컴포넌트 스캔 대상 패키지 목록
     */
    String[] value();
}
