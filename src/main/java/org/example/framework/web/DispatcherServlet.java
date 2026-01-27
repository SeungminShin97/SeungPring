package org.example.framework.web;

import org.example.framework.annotation.Component;
import org.example.framework.was.container.Servlet;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.was.protocol.model.HttpStatus;
import org.example.framework.web.adapter.HandlerAdapter;
import org.example.framework.web.interceptor.HandlerExecutionChain;
import org.example.framework.web.interceptor.HandlerInterceptor;
import org.example.framework.web.mapping.HandlerMapping;

import java.util.List;

/**
 * {@code DispatcherServlet}은 요청 처리의 중앙 조율자로서,
 * {@link HandlerMapping}을 통해 Handler를 찾고
 * {@link HandlerAdapter}를 통해 이를 실행한다.
 *
 * <p>실제 요청 처리 로직은 각 전략 객체에 위임하며,
 * 자신은 전체 흐름을 조율하는 역할만 담당한다.</p>
 */
@Component
public class DispatcherServlet implements Servlet {

    private final List<HandlerMapping> handlerMappings;
    private final List<HandlerAdapter> handlerAdapters;

    /**
     * DispatcherServlet을 생성한다.
     *
     * <p>요청을 처리할 {@link HandlerMapping}과 {@link HandlerAdapter} 목록을
     * 주입받아 내부에 보관한다.</p>
     *
     * @param handlerMappings 요청에 대응하는 Handler를 찾기 위한 매핑 전략들
     * @param handlerAdapters Handler를 실행하기 위한 어댑터 전략들
     */
    public DispatcherServlet(List<HandlerMapping> handlerMappings, List<HandlerAdapter> handlerAdapters) {
        this.handlerMappings = handlerMappings;
        this.handlerAdapters = handlerAdapters;
    }

    /**
     * WAS로부터 전달된 HTTP 요청을 처리한다.
     *
     * <p>요청 처리는 내부적으로 {@link #doDispatch(HttpRequest, HttpResponse)}에
     * 위임된다.</p>
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @throws Exception 요청 처리 중 발생한 예외
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        try {
            doDispatch(request, response);
        } catch (IllegalStateException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.writeBody(HttpStatus.NOT_FOUND.reason());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.writeBody(HttpStatus.BAD_REQUEST.reason());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.writeBody(HttpStatus.INTERNAL_SERVER_ERROR.reason());
        }
    }

    /**
     * 실제 요청 디스패치 로직을 수행한다.
     *
     * <p>요청에 매핑되는 Handler를 조회한 뒤,
     * 이를 실행할 수 있는 {@link HandlerAdapter}를 찾아
     * Handler 실행을 위임한다.</p>
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @throws Exception Handler 실행 중 발생한 예외
     */
    protected void doDispatch(HttpRequest request, HttpResponse response) throws Exception {
        HandlerExecutionChain chain = getHandler(request);
        Object handler = chain.getHandler();

        Exception dispatchException = null;

        try {
            // preHandle
            for(HandlerInterceptor interceptor : chain.getInterceptors()) {
                if(!interceptor.preHandle(request, response, handler)) {
                    triggerAfterCompletion(chain, request, response, null);
                    return;
                }
            }

            // Handler
            HandlerAdapter adapter = getHandlerAdapter(handler);
            adapter.handle(request, response, handler);

            // postHandle
            for(HandlerInterceptor interceptor : chain.getInterceptors())
                interceptor.postHandle(request, response, handler);
        } catch (Exception e) {
            dispatchException = e;
            throw e;
        } finally {
            // afterCompletion
            triggerAfterCompletion(chain, request, response, dispatchException);
        }
    }

    /**
     * 인터셉터 체인의 {@link HandlerInterceptor#afterCompletion}을 호출한다.
     *
     * <p>
     * {@code afterCompletion}은 요청 처리 결과와 관계없이
     * 반드시 한 번 호출되며,
     * 인터셉터 등록 순서의 역순으로 실행된다.
     * </p>
     *
     * <p>
     * 이 메서드는 예외 발생 여부를 인터셉터에 전달하여
     * 리소스 정리, 트레이싱 종료 등의 후처리를 가능하게 한다.
     * </p>
     *
     * @param chain    현재 요청에 대한 실행 체인
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param ex       요청 처리 중 발생한 예외 (없으면 {@code null})
     * @throws Exception afterCompletion 처리 중 발생한 예외
     */
    private void triggerAfterCompletion(HandlerExecutionChain chain, HttpRequest request, HttpResponse response, Exception ex) throws Exception{
        List<HandlerInterceptor> interceptors = chain.getInterceptors();
        for(int i = interceptors.size() - 1; i >= 0; i--)
            interceptors.get(i).afterCompletion(request, response, chain.getHandler(), ex);
    }

    /**
     * 주어진 요청에 대응하는 Handler를 조회한다.
     *
     * <p>
     * 실행 체인은 실제 Handler와 함께,
     * 해당 요청에 적용될 {@link HandlerInterceptor} 목록을 포함한다.
     * </p>
     *
     * @param request 현재 HTTP 요청
     * @return 요청에 매핑된 {@link HandlerExecutionChain}
     * @throws IllegalStateException 매칭되는 Handler가 없는 경우
     */
    protected HandlerExecutionChain getHandler(HttpRequest request) {
        for (HandlerMapping mapping : handlerMappings) {
            HandlerExecutionChain chain = mapping.getHandler(request);
            if(chain != null) return chain;
        }
        throw new IllegalStateException("No handler found for " + request.getMethod() + " " + request.getPath());
    }

    /**
     * 주어진 Handler를 실행할 수 있는 {@link HandlerAdapter}를 조회한다.
     *
     * <p>등록된 어댑터 중 {@link HandlerAdapter#supports(Object)}가
     * {@code true}를 반환하는 어댑터를 선택한다.</p>
     *
     * @param handler 실행 대상 Handler
     * @return Handler를 실행할 수 있는 HandlerAdapter
     * @throws IllegalStateException 지원하는 어댑터가 없는 경우
     */
    protected HandlerAdapter getHandlerAdapter(Object handler) {
        for(HandlerAdapter adapter : handlerAdapters) {
            if(adapter.supports(handler))
                return adapter;
        }
        throw new IllegalStateException("No adapter found");
    }
}
