package org.example.framework.web;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.*;
import org.example.framework.web.adapter.HandlerAdapter;
import org.example.framework.web.mapping.HandlerMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DispatcherServletTest {

    private DispatcherServlet dispatcher;
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;

    private HttpRequest request;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        handlerMapping = mock(HandlerMapping.class);
        handlerAdapter = mock(HandlerAdapter.class);

        dispatcher = new DispatcherServlet(
                List.of(handlerMapping),
                List.of(handlerAdapter)
        );

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
    @DisplayName("정상 요청은 HandlerMapping → HandlerAdapter → handle 흐름을 탄다")
    void dispatch_success() throws Exception {
        Object handler = new Object();

        when(handlerMapping.getHandler(request)).thenReturn(handler);
        when(handlerAdapter.supports(handler)).thenReturn(true);

        dispatcher.service(request, response);

        verify(handlerAdapter).handle(request, response, handler);
    }

    @Test
    @DisplayName("Handler가 없으면 404 NOT_FOUND")
    void dispatch_noHandler() throws Exception {
        when(handlerMapping.getHandler(request)).thenReturn(null);

        dispatcher.service(request, response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertTrue(response.getBody().getAsString("UTF-8")
                .contains(HttpStatus.NOT_FOUND.reason()));
    }

    @Test
    @DisplayName("Handler는 있으나 Adapter가 없으면 500 INTERNAL_SERVER_ERROR")
    // TODO: Adapter가 없을 경우 500으로 내려야 한다 (현재는 404)
    void dispatch_noAdapter() throws Exception {
        Object handler = new Object();

        when(handlerMapping.getHandler(request)).thenReturn(handler);
        when(handlerAdapter.supports(handler)).thenReturn(false);

        dispatcher.service(request, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue(response.getBody().getAsString("UTF-8")
                .contains(HttpStatus.INTERNAL_SERVER_ERROR.reason()));
    }

    @Test
    @DisplayName("HandlerAdapter에서 IllegalArgumentException 발생 시 400 BAD_REQUEST")
    void dispatch_badRequest() throws Exception {
        Object handler = new Object();

        when(handlerMapping.getHandler(request)).thenReturn(handler);
        when(handlerAdapter.supports(handler)).thenReturn(true);
        doThrow(new IllegalArgumentException("bad"))
                .when(handlerAdapter)
                .handle(request, response, handler);

        dispatcher.service(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    @DisplayName("HandlerAdapter에서 일반 Exception 발생 시 500 INTERNAL_SERVER_ERROR")
    void dispatch_internalError() throws Exception {
        Object handler = new Object();

        when(handlerMapping.getHandler(request)).thenReturn(handler);
        when(handlerAdapter.supports(handler)).thenReturn(true);
        doThrow(new RuntimeException("boom"))
                .when(handlerAdapter)
                .handle(request, response, handler);

        dispatcher.service(request, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
    }
}