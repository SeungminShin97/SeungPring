package org.example.framework.web.mapping;

import org.example.framework.core.lifecycle.ApplicationContextAware;
import org.example.framework.core.lifecycle.SmartInitializingSingleton;
import org.example.framework.was.protocol.model.HttpRequest;

/**
 * {@code HandlerMapping}은 HTTP 요청에 대응하는
 * 실행 대상(handler)를 조회하기 위한 전략 인터페이스다.
 *
 * <p>구현체는 요청 정보({@link HttpRequest})를 기반으로
 * 적절한 Handler를 탐색하며,
 * 매핑되지 않은 경우 {@code null}을 반환한다.</p>
 */
public interface HandlerMapping extends ApplicationContextAware, SmartInitializingSingleton {

    /**
     * 주어진 요청에 매핑되는 Handler를 반환한다.
     *
     * @param request 현재 HTTP 요청
     * @return 매칭되는 Handler, 없으면 {@code null}
     */
    Object getHandler(HttpRequest request);
}
