package org.example.framework.core;

import java.util.List;

public interface ListableBeanFactory {

    /**
     * 지정된 타입에 할당 가능한
     * 모든 Bean 인스턴스를 조회합니다.
     *
     * <p>
     * 이 메서드는 Bean 조회 흐름 중
     * <strong>다중 Bean 수집 단계</strong>에 해당하며,
     * 단일 Bean 조회 실패 여부와 무관하게
     * 조건에 부합하는 모든 Bean을 반환합니다.
     * </p>
     *
     * <p>
     * 반환되는 리스트의 순서는 보장되지 않으며,
     * 주입 순서가 중요한 경우
     * 별도의 정렬 또는 우선순위 정책이 필요합니다.
     * </p>
     *
     * @param type 조회 대상이 되는 Bean 타입
     * @param <T> 조회할 Bean의 타입
     * @return 해당 타입에 할당 가능한 모든 Bean 목록 (없을 경우 빈 리스트)
     */
    <T> List<T> getBeansOfType(Class<T> type);
}
