package org.example.framework.was.connector;

import org.example.framework.was.endpoint.AbstractEndpoint;
import org.example.framework.was.endpoint.BioEndpoint;
import org.example.framework.was.endpoint.VirtualBioEndpoint;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.example.framework.LifeCycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class Connector implements LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    /** 사용 포트 */
    private final int port;
    /** 통신 방식 */
    private final AbstractEndpoint endpoint;
    /** HTTP Protocol Selector */
    private final HttpProtocolSelector selector;
    /** HTTP Handler Factory */
    private final HttpProtocolHandlerFactory handlerFactory;

    public Connector(int port, ExecutorService executor, boolean isVirtual, HttpProtocolSelector selector, HttpProtocolHandlerFactory handlerFactory) {
        this.selector = selector;
        this.handlerFactory = handlerFactory;

        if(!isVirtual) {
            this.endpoint = new BioEndpoint(
                    port,
                    executor,
                    selector,
                    handlerFactory
            );
        } else {
            this.endpoint = new VirtualBioEndpoint(
                    port,
                    executor,
                    selector,
                    handlerFactory
            );
        }
        this.port = port;
    }

    /**
     * 서버를 시작한다.
     * 내부 Endpoint.start() 호출만 수행.
     */
    @Override
    public void start() {
        try {
            log.info("[Connector] Starting server on port {}", port);
            endpoint.start();
        } catch (IOException e) {
            throw new RuntimeException("[Connector] Failed to start server: " + e.getMessage(), e);
        }
    }

    /**
     * 서버를 중지한다.
     */
    @Override
    public void stop() {
        try {
            log.info("[Connector] Stopping server on port {}", port);
            endpoint.stop();
        } catch (IOException e) {
            log.error("[Connector] Failed to stop server: {}", e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }

}
