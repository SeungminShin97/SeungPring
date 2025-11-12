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
     * <p>반환 시 새로운 {@link ArrayList}로 복사하여 반환합니다.</p>
     *
     * @param key 헤더 이름
     * @return 헤더 값 목록의 복사본, 없으면 {@code Collections.emptyList()} 반환
     */
    public List<String> get(String key) {
        List<String> list = headers.get(key);
        // 원본 리스트가 null이면 빈 리스트 반환, 아니면 새로운 리스트로 복사하여 반환
        return (list == null) ? Collections.emptyList() : new ArrayList<>(list);
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
     * 반환된 맵은 원본 데이터를 깊은 복사(Deep Copy)한 후
     * 읽기 전용으로 감싸서 반환한다.
     *
     * @return 읽기 전용 헤더 맵의 깊은 복사본
     */
    public Map<String, List<String>> getAll() {
        Map<String, List<String>> deepCopiedMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : headers.entrySet())
            deepCopiedMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));

        return Collections.unmodifiableMap(deepCopiedMap);
    }
}
