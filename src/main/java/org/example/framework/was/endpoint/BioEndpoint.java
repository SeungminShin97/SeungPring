package org.example.framework.was.endpoint;

import org.example.framework.was.processor.SocketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * 블로킹 I/O 기반 Endpoint 구현체.
 * <p>
 * 내부적으로 {@link ServerSocket}을 사용하여 클라이언트 연결을 수락한다.
 * 각 연결은 {@link SocketProcessor}에 의해 처리된다.
 *
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/tomcat/util/net/JIoEndpoint.java">
 * Apache Tomcat JIoEndpoint.java</a>
 */
public class BioEndpoint extends AbstractEndpoint{
    private static final Logger log = LoggerFactory.getLogger(BioEndpoint.class);

    private final ExecutorService executor;
    private ServerSocket serverSocket;

    public BioEndpoint(int port, ExecutorService executor) {
        super(port);
        this.executor = executor;
    }

    @Override
    protected void bind() throws IOException {
        this.serverSocket = new ServerSocket(getPort());
        log.info("[BioEndpoint] Bound to port {}", getPort());
    }

    @Override
    protected void acceptLoop() throws IOException {
        log.info("[BioEndpoint] Waiting for client connections...");

        while(isRunning()) {
            try {
                // 블로킹 accept
                Socket clientSocket = serverSocket.accept();
                log.info("[BioEndpoint] Accepted {}", clientSocket.getRemoteSocketAddress());
                // 요청 처리
                executor.execute(new SocketProcessor(clientSocket));
            } catch (IOException e) {
                if(isRunning())
                    log.info("[BioEndpoint] Error accepting connection: {}", e.getMessage());
            }
        }
        log.info("[BioEndpoint] Acceptor loop terminated");
    }

    @Override
    protected void close() throws IOException {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log.info("[BioEndpoint] Server socket closed");
            }
        } catch (IOException e) {
            log.warn("[BioEndpoint] Error while closing server socket: {}", e.getMessage());
        }
    }
}
