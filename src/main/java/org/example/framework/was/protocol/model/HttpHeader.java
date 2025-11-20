package org.example.framework.was.protocol.model;

import org.example.framework.was.utils.HeaderNameFormatter;

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
     * HTTP 헤더 필드를 추가합니다.
     * <p>
     * 키(key)는 대소문자를 구분하지 않도록 소문자로 정규화하여 저장합니다.
     * 값(value)은 대소문자를 유지하며 앞뒤 공백만 제거하여 저장합니다.
     * 쉼표(,)로 구분된 여러 값이라도 분리하지 않고 전체 문자열로 저장합니다.
     * </p>
     *
     * @param key   헤더 필드 이름 (예: "Content-Type")
     * @param value 헤더 필드 값 (예: "text/plain, charset=UTF-8")
     */
    public void put(String key, String value) {
        String normalizedKey = key.toLowerCase().trim();
        String trimmedValue = value.trim();

        List<String> list = headers.computeIfAbsent(normalizedKey, k -> new ArrayList<>());
        list.add(trimmedValue);
    }


    /**
     * 지정한 헤더 이름에 해당하는 모든 값을 반환한다.
     * <p>조회 시 키의 대소문자를 구분하지 않습니다.</p>
     * <p>반환 시 새로운 {@link ArrayList}로 복사하여 반환합니다.</p>
     *
     * @param key 헤더 이름
     * @return 헤더 값 목록의 복사본, 없으면 {@code Collections.emptyList()} 반환
     */
    public List<String> get(String key) {
        List<String> list = headers.get(key.toLowerCase().trim());

        return (list == null) ? Collections.emptyList() : new ArrayList<>(list);
    }


    /**
     * 지정한 헤더 이름에 해당하는 첫 번째 값을 반환한다.
     * <p>조회 시 키의 대소문자를 구분하지 않습니다.</p>
     *
     * @param key 헤더 이름
     * @return 첫 번째 값, 없으면 {@code null}
     */
    public String getFirst(String key) {
        List<String> list = headers.get(key.toLowerCase().trim());

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
            deepCopiedMap.put(HeaderNameFormatter.toTitleCase(entry.getKey()), new ArrayList<>(entry.getValue()));

        return Collections.unmodifiableMap(deepCopiedMap);
    }
}
