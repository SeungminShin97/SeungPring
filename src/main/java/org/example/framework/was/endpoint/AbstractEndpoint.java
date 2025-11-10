package org.example.framework.was.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Apache Tomcat의 AbstractEndpoint 를 참고
 * <p>
 * Tomcat에서는 Endpoint가 ServerSocket(또는 Channel)을 열고,
 * 클라이언트 연결을 수락(accept)한 뒤, 프로토콜 핸들러로 넘겨주는 역할을 한다.
 * <p>
 * BIO/NIO 공통 부모로 사용한다.
 * 즉, 실제 통신 방식(BIO/NIO)은 하위 클래스가 구현하고,
 * 공통된 서버 생명주기(start, stop)는 이 추상 클래스가 관리한다.
 *
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/tomcat/util/net/AbstractEndpoint.java">
 * Apache Tomcat AbstractEndpoint.java</a>
 */
public abstract class AbstractEndpoint {
    private final Logger log = LoggerFactory.getLogger(AbstractEndpoint.class);

    /** 수신 포트 */
    private final int port;

    /** 서버 실행 상태 */
    private volatile boolean running = false;

    /** 클라이언트 연결 수락 스레드 */
    private Thread acceptorThread;

    public AbstractEndpoint(int port) {
        this.port = port;
    }


    /**
     * 실제 소켓을 열고 포트에 바인딩한다.
     * <p>
     * BIO라면 ServerSocket.bind(), NIO라면 ServerSocketChannel.bind() 수행.
     * 이 단계에서 예외가 발생하면 서버는 시작되지 않는다.
     */
    protected abstract void bind() throws IOException;

    /**
     * 클라이언트 연결을 지속적으로 수락하는 루프.
     * <p>
     * 하위 클래스에서는 이 안에서 while(isRunning()) 루프를 돌며
     * Socket 또는 SocketChannel을 accept() 해야 한다.
     * <p>
     * 각 연결은 SocketProcessor 등으로 위임되어 요청을 처리하게 된다.
     */
    protected abstract void acceptLoop() throws IOException;

    /**
     * 소켓 및 네트워크 리소스를 정리한다.
     * <p>
     * ServerSocket.close() 또는 Selector.close() 등을 수행.
     * stop() 호출 시 자동으로 실행된다.
     */
    protected abstract void close() throws IOException;


    /**
     * 서버를 시작한다.
     * <p>
     * 1. bind()로 포트를 열고
     * 2. running 플래그를 true로 전환한 후
     * 3. Acceptor 스레드를 생성해 acceptLoop()를 실행한다.
     */
    public void start() throws IOException {
        bind();
        running = true;

        acceptorThread = new Thread(() -> {
            try {
                acceptLoop();
            } catch (IOException e) {
                if (running)
                    log.error("[Endpoint] Acceptor stopped due to error: " + e.getMessage());
                // TODO: 재시도 로직 추가
            }
        }, "Acceptor-" + port);

        acceptorThread.start();
        log.info("[Endpoint] Listening on port " + port);
    }

    /**
     * 서버를 중지한다.
     * <p>
     * running 플래그를 false로 바꾸고,
     * 소켓을 닫고, Acceptor 스레드를 인터럽트한다.
     */
    public void stop() throws IOException {
        running = false;
        close();
        if (acceptorThread != null && acceptorThread.isAlive()) {
            acceptorThread.interrupt();
        }
        log.info("[Endpoint] Stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    /** 포트 정보를 InetSocketAddress 형태로 반환 */
    protected InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(port);
    }
}
