package org.example.framework.core;

import org.example.framework.context.MyApplicationContext;

/**
 * {@link ApplicationContext}는 애플리케이션 전체 컨텍스트를 대표하는 인터페이스다.
 * <br>
 * 컨텍스트의 메타정보(이름, 식별자, 시작 시각 등)를 제공한다.
 * <br>
 * 스프링의 {@code org.springframework.context.ApplicationContext}를 단순화한 형태이며,
 * 구현체{@link MyApplicationContext}는 Bean 등록, 생성, 의존성 주입 등의 실제 로직을 담당한다.
 * <br>
 * {@see <a href="https://docs.spring.io/spring-framework/docs/5.3.22/javadoc-api/org/springframework/context/ApplicationContext.html">공식문서</a>}
 */
public interface ApplicationContext {

    /**
     * 애플리케이션 이름을 반환한다.
     * 일반적으로 실행 환경 또는 설정 파일 이름을 의미한다.
     *
     * @return 애플리케이션 이름 (null 가능)
     */
    String getApplicationName();

    /**
     * 컨텍스트를 설명하는 문자열을 반환한다.
     * 디버깅이나 로깅 시 사용된다.
     *
     * @return 컨텍스트 설명 문자열
     */
    String getDisplayName();

    /**
     * 컨텍스트를 고유하게 식별하기 위한 ID를 반환한다.
     * 동일한 애플리케이션 내 여러 컨텍스트 구분에 사용된다.
     *
     * @return 컨텍스트 ID
     */
    String getId();

    /**
     * 컨텍스트가 시작된 시간을 반환한다.
     *
     * @return 컨텍스트 시작 시각 (epoch millisecond)
     */
    long getStartupDate();
}
