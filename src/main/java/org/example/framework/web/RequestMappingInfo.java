package org.example.framework.web;

import org.example.framework.was.protocol.model.HttpMethod;

/**
 * {@code RequestMappingInfo}는 요청 경로와 HTTP 메서드로 구성된
 * 요청 매핑 조건을 표현하는 값 객체다.
 *
 * <p>{@link org.example.framework.web.mapping.HandlerMapping}에서
 * 요청을 식별하기 위한 키로 사용된다.</p>
 */
public record RequestMappingInfo(
        String path,
        HttpMethod method
) {
}
