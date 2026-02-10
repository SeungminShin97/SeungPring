package org.example.framework.was.endpoint;

import org.example.framework.was.processor.SocketProcessor;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualBioEndpoint extends AbstractEndpoint {
    private static final Logger log = LoggerFactory.getLogger(VirtualBioEndpoint.class);

    private ServerSocket serverSocket;
    private final HttpProtocolSelector selector;
    private final HttpProtocolHandlerFactory handlerFactory;

    // 가상 스레드를 사용하는 ExecutorService
    private final ExecutorService executor;

    public VirtualBioEndpoint(int port, ExecutorService executor, HttpProtocolSelector selector, HttpProtocolHandlerFactory handlerFactory) {
        super(port);
        this.selector = selector;
        this.handlerFactory = handlerFactory;
        this.executor = executor;
    }

    @Override
    protected void bind() throws IOException {
        this.serverSocket = new ServerSocket(getPort());
        log.info("[VirtualBioEndpoint] Bound to port {}", getPort());
    }

    @Override
    protected void acceptLoop() throws IOException {
        log.info("[VirtualBioEndpoint] Waiting for client connections...");

        while (isRunning()) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("[VirtualBioEndpoint] Accepted {}", clientSocket.getRemoteSocketAddress());

                executor.execute(new SocketProcessor(clientSocket, selector, handlerFactory));
            } catch (IOException e) {
                if (isRunning()) log.error("[VirtualBioEndpoint] Accept error", e);
            }
        }
    }

    @Override
    protected void close() throws IOException {
        if (serverSocket != null) serverSocket.close();
        if (executor != null) executor.shutdown(); // 리소스 정리 필수
        log.info("[VirtualBioEndpoint] Resources closed");
    }
}