package org.example.framework.infrastructure.was;

import org.example.framework.was.adapter.DefaultServletAdapter;
import org.example.framework.was.connector.Connector;
import org.example.framework.was.container.Servlet;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.example.framework.LifeCycle.LifeCycle;
import org.example.framework.infrastructure.application.SeungPringApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WasInfrastructure implements LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(WasInfrastructure.class);

    private final ExecutorService executor;
    private final Connector connector;

    public WasInfrastructure(SeungPringApplicationConfig config, Servlet servlet) {
        this.executor = Executors.newFixedThreadPool(config.workerThreads());

        HttpProtocolSelector selector = new HttpProtocolSelector();

        HttpProtocolHandlerFactory handlerFactory =
                HttpProtocolHandlerFactory.create(new DefaultServletAdapter(servlet));

        this.connector = new Connector(
                config.port(),
                executor,
                selector,
                handlerFactory
        );
    }

    @Override
    public void start() throws Exception {
        log.info("[WAS] Initializing server resources");
        connector.start();
        log.info("[WAS] Server initialization complete");
    }

    @Override
    public void stop() throws Exception {
        log.info("[WAS] Shutting down server");
        connector.stop();
        executor.shutdown();
        log.info("[WAS] Server shutdown complete");
    }
}
