package org.example.framework.was.protocol.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.UnsupportedCharsetException;

import static org.junit.jupiter.api.Assertions.*;

class HttpBodyTest {

    @Test
    @DisplayName("생성자에 전달된 바이트 배열은 방어적으로 복사되어 외부 변경의 영향을 받지 않는다")
    void should_defensively_copy_input_byte_array() {
        // given
        byte[] source = "hello".getBytes();
        HttpBody body = new HttpBody(source);

        // when
        source[0] = 'H';

        // then
        assertEquals("hello", body.getAsString("UTF-8"));
    }

    @Test
    @DisplayName("empty()로 생성된 바디는 길이가 0이고 비어있는 상태로 간주된다")
    void empty_body_should_have_zero_length_and_be_empty() {
        // given
        HttpBody body = HttpBody.empty();

        // then
        assertTrue(body.isEmpty());
        assertEquals(0, body.getContentLengthLong());
    }

    @Test
    @DisplayName("getAsString은 지정한 문자 인코딩을 사용해 바디를 문자열로 변환한다")
    void should_convert_body_to_string_using_given_charset() {
        // given
        byte[] data = "안녕".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        HttpBody body = new HttpBody(data);

        // when
        String result = body.getAsString("UTF-8");

        // then
        assertEquals("안녕", result);
    }

    @Test
    @DisplayName("지원하지 않는 문자 인코딩을 지정하면 UnsupportedCharsetException이 발생한다")
    void should_throw_exception_when_charset_is_not_supported() {
        // given
        HttpBody body = new HttpBody("test".getBytes());

        // then
        assertThrows(UnsupportedCharsetException.class,
                () -> body.getAsString("NO-SUCH-CHARSET"));
    }

    @Test
    @DisplayName("getContentLengthLong은 바디 데이터의 전체 바이트 길이를 반환한다")
    void should_return_content_length_in_bytes() {
        // given
        HttpBody body = new HttpBody("hello".getBytes());

        // then
        assertEquals(5, body.getContentLengthLong());
    }

    @Test
    @DisplayName("getData는 내부 바이트 배열의 복사본을 반환하여 외부 변경을 차단한다")
    void getData_should_return_defensive_copy() {
        // given
        HttpBody body = new HttpBody("abc".getBytes());

        // when
        byte[] data = body.getData();
        data[0] = 'z';

        // then
        assertEquals("abc", body.getAsString("UTF-8"));
    }
}
