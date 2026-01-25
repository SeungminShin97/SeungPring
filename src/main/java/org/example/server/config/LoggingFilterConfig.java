package org.example.server.config;

import org.example.framework.annotation.Bean;
import org.example.framework.annotation.Configuration;
import org.example.framework.web.filter.FilterRegistration;
import org.example.framework.web.filter.impl.LoggingFilter;

/**
 * 필터 등록 예시를 위한 설정 클래스
 *
 * <p>
 * {@link LoggingFilter}는 실제 기능 제공 목적이 아닌,
 * 필터 체인 동작 및 순서 확인을 위한
 * 테스트 / 예시용 구현체이다.
 * </p>
 */
@Configuration
public class LoggingFilterConfig {

    /**
     * 테스트용 로깅 필터를 필터 체인에 등록한다.
     *
     * @return 로깅 필터 등록 정보
     */
    @Bean
    public FilterRegistration loggingFilter() {
        return  new FilterRegistration(new LoggingFilter(), 0);
    }
}
