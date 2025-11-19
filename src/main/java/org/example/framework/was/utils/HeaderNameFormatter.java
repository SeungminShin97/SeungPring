package org.example.framework.was.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * HTTP 헤더 이름을 RFC 관례에 맞는 Title-Case 형식으로 변환하는 유틸리티 클래스.
 * <p>
 * 예를 들어, 입력 값이 {@code "content-type"} 또는 {@code "CONTENT-TYPE"}일 경우
 * {@code "Content-Type"} 으로 변환한다.
 * 하이픈('-')으로 구분된 각 단어의 첫 글자를 대문자로, 나머지를 소문자로 정규화한다.
 * </p>
 *
 * <p>
 * 입력이 {@code null} 이거나 빈 문자열일 경우 빈 문자열을 반환한다.
 * </p>
 */
public class HeaderNameFormatter {

    /**
     * 주어진 HTTP 헤더 이름을 Title-Case 형식으로 변환한다.
     * <p>
     * 하이픈('-')을 기준으로 단어를 분리하고, 각 단어의 첫 글자는 대문자로,
     * 이후 문자는 소문자로 변환한 뒤 다시 하이픈으로 결합한다.
     * </p>
     *
     * @param headerName 변환할 헤더 이름
     * @return Title-Case 형식의 헤더 이름. 입력이 {@code null} 또는 빈 문자열이면 빈 문자열 반환
     */
    public static String toTitleCase(String headerName) {
        if(headerName == null || headerName.isEmpty())
            return "";

        return Arrays.stream(headerName.split("-"))
                .map(word -> {
                    if(word.isEmpty()) return "";
                    return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
                })
                .collect(Collectors.joining("-"));
    }
}
