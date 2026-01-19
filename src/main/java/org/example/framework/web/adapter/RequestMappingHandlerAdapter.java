package org.example.framework.web.adapter;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.web.HandlerMethod;

import java.lang.reflect.Method;

/**
 * {@code RequestMappingHandlerAdapter}는
 * {@link org.example.framework.web.HandlerMethod}를 실행하기 위한
 * {@link HandlerAdapter} 구현체다.
 *
 * <p>리플렉션을 사용하여 컨트롤러 메서드를 호출하며,
 * 최소한의 파라미터 바인딩과 반환값 처리를 지원한다.</p>
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter{

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    /**
     * {@link HandlerMethod}를 실행하여 실제 컨트롤러 메서드를 호출한다.
     *
     * <p>지원하는 파라미터 타입은 {@link HttpRequest}, {@link HttpResponse}이며,
     * 반환 타입은 {@code void} 또는 {@link String}만 허용한다.</p>
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler  실행 대상 Handler
     * @return 컨트롤러 메서드의 반환값
     * @throws Exception 메서드 호출 중 발생한 예외
     */
    @Override
    public Object handle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();

        Object[] args = resolveArguments(method, request, response);

        method.setAccessible(true);
        Object returnValue = method.invoke(hm.getBean(), args);

        if(method.getReturnType() == void.class)
            return null;

        if(returnValue instanceof String str) {
            response.writeBody(str);
            return str;
        }

        throw new IllegalStateException(
                "Unsupported return type: " + method.getReturnType() + " in method: " + method
        );
    }

    /**
     * 컨트롤러 메서드의 파라미터 타입을 기반으로
     * 실행 시 전달할 인자 배열을 생성한다.
     *
     * <p>현재는 {@link HttpRequest}, {@link HttpResponse} 타입만 지원한다.</p>
     */
    private Object[] resolveArguments(Method method, HttpRequest request, HttpResponse response) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for(int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];

            if(type.equals(HttpRequest.class))
                args[i] = request;
            else if(type.equals(HttpResponse.class))
                args[i] = response;
            else
                throw new IllegalStateException("Unsupported parameter type: " + type.getName());
        }

        return args;
    }
}
