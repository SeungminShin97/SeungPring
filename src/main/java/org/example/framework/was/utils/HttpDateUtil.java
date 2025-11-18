package org.example.framework.was.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP 헤더용 날짜 문자열을 생성하는 유틸리티 클래스.
 * <p>
 * HTTP/1.1 표준(RFC 7231 / RFC 1123)에 따라,
 * Date 헤더는 반드시 UTC(GMT) 기준의 RFC1123 형식으로 출력되어야 한다.
 * <br>
 * 본 클래스는 현재 서버 시각을 UTC로 변환한 후
 * RFC1123 규격 문자열로 포맷하여 반환한다.
 *
 * <pre>
 * 예) "Wed, 22 Nov 2025 12:34:56 GMT"
 * </pre>
 */
public class HttpDateUtil {

    /**
     * 현재 서버 시각을 UTC 기준 RFC1123 형식으로 변환하여 반환한다.
     *
     * @return RFC1123 포맷의 UTC 날짜 문자열
     */
    public static String now() {
        return ZonedDateTime.now(ZoneId.of("UTC"))
                .format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
