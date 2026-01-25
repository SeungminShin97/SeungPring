package org.example.framework.web.filter;

/**
 * {@link Filter}와 해당 필터의 실행 순서를 함께 표현하는 등록 정보 객체
 *
 * <p>
 * 필터 체인 구성 시, {@code order} 값을 기준으로 정렬되어
 * 실제 실행 순서가 결정된다.
 * </p>
 *
 * <p>
 * 이 클래스는 필터 자체의 동작에는 관여하지 않으며,
 * 필터 등록 및 정렬을 위한 메타데이터 역할만 수행한다.
 * </p>
 */
public class FilterRegistration {

    private final Filter filter;
    private final int order;

    /**
     * 필터 등록 정보를 생성한다.
     *
     * @param filter 등록할 필터
     * @param order  필터 실행 순서 (낮을수록 먼저 실행)
     */
    public FilterRegistration(Filter filter, int order) {
        this.filter = filter;
        this.order = order;
    }

    /**
     * 등록된 필터를 반환한다.
     */
    public Filter filter() {
        return filter;
    }

    /**
     * 필터 실행 순서를 반환한다.
     */
    public int order() {
        return order;
    }
}
