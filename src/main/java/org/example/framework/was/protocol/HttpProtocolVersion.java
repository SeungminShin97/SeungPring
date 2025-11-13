package org.example.framework.was.protocol;

/**
 * WAS가 지원하는 HTTP 프로토콜 버전을 명시하는 Enum입니다.
 * <p>
 * 숫자로 된 매직 넘버(magic number) 대신 명확한 상수를 제공하여 코드의 가독성과 안정성을 높입니다.
 */
public enum HttpProtocolVersion {

    HTTP_1_1(1, "HTTP/1.1"),
    HTTP_2_0(2, "HTTP/2.0");

    private final int version;
    private final String protocolString;

    HttpProtocolVersion(int version, String protocolString) {
        this.version = version;
        this.protocolString = protocolString;
    }

    public int getVersion() {
        return version;
    }

    public String getProtocolString() {
        return protocolString;
    }
}