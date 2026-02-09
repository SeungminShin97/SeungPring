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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class WasInfrastructure implements LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(WasInfrastructure.class);

    private ScheduledExecutorService monitor;
    private final AtomicInteger rejectedCount = new AtomicInteger();

    private final ExecutorService executor;
    private final Connector connector;

    public WasInfrastructure(SeungPringApplicationConfig config, Servlet servlet) {
        // 기본 스레드풀
//        this.executor = Executors.newFixedThreadPool(config.workerThreads());

//        // 무제한 큐
//        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
//        this.executor = new ThreadPoolExecutor(
//                config.workerThreads(), // core
//                config.workerThreads(), // max
//                0L,
//                TimeUnit.MILLISECONDS,
//                queue,
//                new ThreadPoolExecutor.AbortPolicy()
//        );

        // 제한 큐 + Fail-fast
        BlockingQueue<Runnable> queue =
                new ArrayBlockingQueue<>(30);

        AtomicInteger rejectedCount = new AtomicInteger();

        RejectedExecutionHandler handler = (r, exec) -> {
            rejectedCount.incrementAndGet();
            throw new RejectedExecutionException("BIO executor saturated");
        };
        this.executor = new ThreadPoolExecutor(
                config.workerThreads(),
                config.workerThreads(),
                0L,
                TimeUnit.MILLISECONDS,
                queue,
                handler
        );

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

        monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(() -> {
            if (executor instanceof ThreadPoolExecutor tpe) {
                log.info(
                        "[EXECUTOR] active={} queue={} rejected={}",
                        tpe.getActiveCount(),
                        tpe.getQueue().size(),
                        rejectedCount.get()
                );
            }
        }, 0, 1, TimeUnit.SECONDS);

        connector.start();
        log.info("[WAS] Server initialization complete");
    }

    @Override
    public void stop() throws Exception {
        log.info("[WAS] Shutting down server");
        connector.stop();
        executor.shutdown();

        if(monitor != null)
            monitor.shutdown();

        log.info("[WAS] Server shutdown complete");
    }
}
