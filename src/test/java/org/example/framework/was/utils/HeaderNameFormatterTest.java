package org.example.framework.was.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.framework.was.utils.HeaderNameFormatter.*;

class HeaderNameFormatterTest {

    @Test
    @DisplayName("null 값이 주어지면 빈 문자열을 반환해야 한다")
    void should_return_empty_string_when_null_is_given() {

        assertEquals("", toTitleCase(null));
    }
    
    @Test
    @DisplayName("빈 문자열이 주어지면 빈 문자열을 반환해야 한다")
    void should_return_empty_string_when_empty_string_is_given() {

        assertEquals("", toTitleCase(""));
    }
    
    @Test
    @DisplayName("문자열이 주어지면 Title Case로 변환해야 한다")
    void should_convert_to_title_case_when_string_is_given() {
        // given
        // when
        // then
        assertEquals("Content-Type", toTitleCase("content-type"));
        assertEquals("X-Custom-Header", toTitleCase("x-custom-header"));
        assertEquals("Host", toTitleCase("HOST"));
        assertEquals("Content-Length", toTitleCase("CoNteNT-LeNGTH"));
        assertEquals("A--B", toTitleCase("a--b"));
    }
}