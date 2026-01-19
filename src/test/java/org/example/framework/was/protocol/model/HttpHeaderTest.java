package org.example.framework.was.protocol.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpHeaderTest {

    @Test
    @DisplayName("헤더 이름은 대소문자를 구분하지 않고 조회할 수 있다")
    void should_ignore_case_when_put_and_get() {
        // given
        HttpHeader headers = new HttpHeader();

        // when
        headers.put("Content-Type", "text/plain");

        // then
        assertEquals("text/plain", headers.getFirst("content-type"));
        assertEquals("text/plain", headers.getFirst("CONTENT-TYPE"));
    }

    @Test
    @DisplayName("동일한 헤더 이름에 여러 개의 값을 순서대로 저장할 수 있다")
    void should_store_multiple_values_for_same_header_name() {
        // given
        HttpHeader headers = new HttpHeader();

        // when
        headers.put("Set-Cookie", "a=1");
        headers.put("Set-Cookie", "b=2");

        // then
        assertEquals(List.of("a=1", "b=2"), headers.get("set-cookie"));
    }

    @Test
    @DisplayName("헤더 값은 앞뒤 공백만 제거하고 대소문자는 그대로 유지한다")
    void should_trim_value_but_preserve_case() {
        // given
        HttpHeader headers = new HttpHeader();

        // when
        headers.put("X-Test", "  AbC  ");

        // then
        assertEquals("AbC", headers.getFirst("x-test"));
    }

    @Test
    @DisplayName("쉼표로 구분된 헤더 값은 분리하지 않고 하나의 문자열로 저장한다")
    void should_not_split_comma_separated_values() {
        // given
        HttpHeader headers = new HttpHeader();

        // when
        headers.put("Accept", "text/plain, text/html");

        // then
        assertEquals("text/plain, text/html", headers.getFirst("accept"));
    }

    @Test
    @DisplayName("get()은 내부 상태를 보호하기 위해 값 목록의 복사본을 반환한다")
    void get_should_return_copy_not_internal_list() {
        // given
        HttpHeader headers = new HttpHeader();
        headers.put("X-Test", "1");

        // when
        List<String> values = headers.get("x-test");
        values.add("2");

        // then
        assertEquals(List.of("1"), headers.get("x-test"));
    }

    @Test
    @DisplayName("getAll()은 Title-Case 헤더 이름을 사용한 읽기 전용 맵을 반환한다")
    void getAll_should_return_unmodifiable_map_with_title_case_keys() {
        // given
        HttpHeader headers = new HttpHeader();
        headers.put("content-type", "text/plain");

        // when
        Map<String, List<String>> all = headers.getAll();

        // then
        assertTrue(all.containsKey("Content-Type"));
        assertEquals(List.of("text/plain"), all.get("Content-Type"));

        assertThrows(UnsupportedOperationException.class,
                () -> all.put("X-Test", List.of("value")));
    }
}
