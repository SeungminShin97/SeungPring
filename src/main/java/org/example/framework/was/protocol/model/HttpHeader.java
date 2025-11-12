package org.example.framework.was.protocol.model;

import java.util.*;


/**
 * HTTP 요청 또는 응답의 헤더를 관리하는 클래스.
 * <p>
 * 하나의 헤더 이름에 여러 개의 값이 존재할 수 있으며,
 * 각 이름은 {@link List} 형태로 저장된다.
 * 파싱 시 쉼표(,)로 구분된 값들은 자동으로 분리되어 추가된다.
 */
public class HttpHeader {

    private final Map<String, List<String>> headers = new HashMap<>();

    /**
     * 헤더를 추가한다. 동일한 키가 이미 존재할 경우 값이 누적된다.
     * 쉼표(,)로 구분된 다중 값은 개별 항목으로 분리하여 저장한다.
     *
     * @param key   헤더 이름
     * @param value 헤더 값 (쉼표로 구분된 문자열 가능)
     */
    public void put(String key, String value) {
        List<String> list = headers.computeIfAbsent(key, k -> new ArrayList<>());

        for (String v : value.split(",")) {
            list.add(v.trim());
        }
    }


    /**
     * 지정한 헤더 이름에 해당하는 모든 값을 반환한다.
     *
     * @param key 헤더 이름
     * @return 헤더 값 목록, 없으면 {@code null}
     */
    public List<String> get(String key) {
        return headers.get(key);
    }


    /**
     * 지정한 헤더 이름에 해당하는 첫 번째 값을 반환한다.
     *
     * @param key 헤더 이름
     * @return 첫 번째 값, 없으면 {@code null}
     */
    public String getFirst(String key) {
        List<String> list = headers.get(key);
        return (list == null || list.isEmpty()) ? null : list.getFirst();
    }

    /**
     * 전체 헤더 맵을 읽기 전용 형태로 반환한다.
     * <p>
     * 반환된 맵은 원본 데이터를 복사하지 않으며,
     * 수정 시 {@link UnsupportedOperationException}이 발생한다.
     *
     * @return 읽기 전용 헤더 맵
     */
    public Map<String, List<String>> getAll() {
        return Collections.unmodifiableMap(headers);
    }
}
