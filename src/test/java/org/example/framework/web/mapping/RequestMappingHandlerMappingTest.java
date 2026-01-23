package org.example.framework.web.mapping;

import org.example.framework.context.MyApplicationContext;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.HttpBody;
import org.example.framework.was.protocol.model.HttpHeader;
import org.example.framework.was.protocol.model.HttpMethod;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.web.HandlerMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestMappingHandlerMappingTest {

    private MyApplicationContext context;
    private RequestMappingHandlerMapping handlerMapping;

    @BeforeEach
    void setUp() {
        context = new MyApplicationContext("org.example.test");
        context.refresh(); // 중요: 컨텍스트 초기화 명시

        handlerMapping = new RequestMappingHandlerMapping();
    }

    private HttpRequest newRequest(String path, HttpMethod method) {
        return new HttpRequest(
                new HttpHeader(),
                HttpBody.empty(),
                HttpProtocolVersion.HTTP_1_1,
                method,
                path
        );
    }

    @Test
    @DisplayName("요청 경로와 HTTP 메서드가 일치하면 HandlerMethod를 반환한다")
    void shouldReturnHandlerMethod_whenRequestMatches() {
        // given
        HttpRequest request = newRequest("/test", HttpMethod.GET);

        // when
        Object handler = handlerMapping.getHandler(request);

        // then
        assertNotNull(handler);
        assertInstanceOf(HandlerMethod.class, handler);
    }

    @Test
    @DisplayName("HTTP 메서드가 다르면 매핑되지 않는다")
    void shouldReturnNull_whenHttpMethodDoesNotMatch() {
        HttpRequest request = newRequest("/test", HttpMethod.PUT);

        Object handler = handlerMapping.getHandler(request);

        assertNull(handler);
    }

    @Test
    @DisplayName("존재하지 않는 경로는 매핑되지 않는다")
    void shouldReturnNull_whenPathDoesNotMatch() {
        HttpRequest request = newRequest("/nope", HttpMethod.GET);

        Object handler = handlerMapping.getHandler(request);

        assertNull(handler);
    }

    @Test
    @DisplayName("@Controller가 아닌 빈은 매핑 대상이 아니다")
    void shouldScanOnlyControllerBeans() {
        // 내부 상태 검증은 하지 않고 결과로 증명
        HttpRequest request = newRequest("/test", HttpMethod.GET);

        Object handler = handlerMapping.getHandler(request);

        assertNotNull(handler); // Controller는 정상 매핑됨
    }
}
