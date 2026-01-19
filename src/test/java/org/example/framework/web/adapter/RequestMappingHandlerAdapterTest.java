package org.example.framework.web.adapter;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.*;
import org.example.framework.web.HandlerMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RequestMappingHandlerAdapterTest {

    static class AdapterTestController {

        public void voidHandler(HttpRequest request, HttpResponse response) {
            response.writeBody("ok");
        }

        public String stringHandler(HttpRequest request) {
            return "hello";
        }

        public Integer invalidReturn(HttpRequest request) {
            return 1;
        }

        public void invalidParam(Integer value) {
        }
    }

    private RequestMappingHandlerAdapter adapter;
    private AdapterTestController controller;
    private HttpRequest request;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        adapter = new RequestMappingHandlerAdapter();
        controller = new AdapterTestController();

        request = new HttpRequest(
                new HttpHeader(),
                HttpBody.empty(),
                HttpProtocolVersion.HTTP_1_1,
                HttpMethod.GET,
                "/test"
        );

        response = new HttpResponse(
                new HttpHeader(),
                HttpBody.empty(),
                HttpProtocolVersion.HTTP_1_1,
                HttpStatus.OK
        );
    }

    @Test
    @DisplayName("HandlerMethod를 지원한다")
    void supports_handlerMethod() throws NoSuchMethodException {
        Method method = AdapterTestController.class.getMethod(
                "voidHandler", HttpRequest.class, HttpResponse.class
        );
        HandlerMethod hm = new HandlerMethod(controller, method);

        assertTrue(adapter.supports(hm));
        assertFalse(adapter.supports(new Object()));
    }

    @Test
    @DisplayName("void 반환 메서드는 정상 실행된다")
    void handle_voidReturn() throws Exception {
        Method method = AdapterTestController.class.getMethod(
                "voidHandler", HttpRequest.class, HttpResponse.class
        );
        HandlerMethod hm = new HandlerMethod(controller, method);

        Object result = adapter.handle(request, response, hm);

        assertNull(result);
        assertTrue(response.getBody().getAsString("UTF-8").contains("ok"));
    }

    @Test
    @DisplayName("String 반환 메서드는 response body에 기록된다")
    void handle_stringReturn() throws Exception {
        Method method = AdapterTestController.class.getMethod(
                "stringHandler", HttpRequest.class
        );
        HandlerMethod hm = new HandlerMethod(controller, method);

        Object result = adapter.handle(request, response, hm);

        assertEquals("hello", result);
        assertTrue(response.getBody().getAsString("UTF-8").contains("hello"));
    }

    @Test
    @DisplayName("지원하지 않는 반환 타입은 예외를 던진다")
    void handle_invalidReturnType() throws Exception {
        Method method = AdapterTestController.class.getMethod(
                "invalidReturn", HttpRequest.class
        );
        HandlerMethod hm = new HandlerMethod(controller, method);

        assertThrows(
                IllegalStateException.class,
                () -> adapter.handle(request, response, hm)
        );
    }

    @Test
    @DisplayName("지원하지 않는 파라미터 타입은 예외를 던진다")
    void handle_invalidParameterType() throws Exception {
        Method method = AdapterTestController.class.getMethod(
                "invalidParam", Integer.class
        );
        HandlerMethod hm = new HandlerMethod(controller, method);

        assertThrows(
                IllegalStateException.class,
                () -> adapter.handle(request, response, hm)
        );
    }
}