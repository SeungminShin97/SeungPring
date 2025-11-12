package org.example.framework.was.protocol.model;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

/**
 * HTTP 요청/응답의 바디 데이터를 저장합니다.
 * WAS는 데이터를 파싱하지 않고 원본 바이트 스트림만 유지합니다.
 */
public class HttpBody {
    /** 원본 바이트 데이터 */
    private final byte[] data;

    public HttpBody(byte[] data) {
        // 방어적 복사(defensive copy)를 통해 외부에서 전달받은 배열이 변경되어도
        // 이 HttpBody 인스턴스는 안전하게 유지되도록 합니다.
        this.data = Arrays.copyOf(data, data.length);
    }


    /**
     * 비어 있는 바디 객체를 반환합니다.
     *
     * @return 비어 있는 {@link HttpBody} 인스턴스
     */
    public static HttpBody empty() {
        return new HttpBody(new byte[0]);
    }

    /**
     * 바디 데이터를 지정한 문자 인코딩으로 문자열로 변환합니다.
     *
     * @param encoding 문자 인코딩 이름 (예: "UTF-8", "EUC-KR")
     * @return 변환된 문자열
     * @throws UnsupportedCharsetException 지원하지 않는 인코딩일 경우 발생
     */
    public String getAsString(String encoding) throws UnsupportedCharsetException {
        return new String(data, Charset.forName(encoding));
    }

    /**
     * 바디의 전체 길이를 바이트 단위로 반환합니다.
     *
     * @return 바디 데이터의 길이 (byte 단위)
     */
    public long getContentLengthLong() {
        return data.length;
    }

    /**
     * 바디가 비어있는지 확인합니다. (테스트에서 필요했던 메소드 추가)
     * @return 바디가 비어있으면 true, 아니면 false
     */
    public byte[] getData() {
        return Arrays.copyOf(this.data, this.data.length);
    }

    /**
     * 바디가 비어있는지 확인합니다. (테스트에서 필요했던 메소드 추가)
     * @return 바디가 비어있으면 true, 아니면 false
     */
    public boolean isEmpty() {
        return this.data.length == 0;
    }
}