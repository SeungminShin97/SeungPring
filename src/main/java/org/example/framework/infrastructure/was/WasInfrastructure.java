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

    private final ExecutorService executor;
    private final boolean virtualMode;
    private final AtomicInteger rejectedCount = new AtomicInteger();

    private final Connector connector;

    public WasInfrastructure(SeungPringApplicationConfig config, Servlet servlet) {
        this.virtualMode = config.virtualEnabled();

        if (!virtualMode) {
            // ================= BIO =================
            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(30);

            RejectedExecutionHandler handler = (r, exec) -> {
                rejectedCount.incrementAndGet();
                throw new RejectedExecutionException("BIO executor saturated");
            };

            executor = new ThreadPoolExecutor(
                    config.workerThreads(),
                    config.workerThreads(),
                    0L,
                    TimeUnit.MILLISECONDS,
                    queue,
                    handler
            );
        } else {
            executor = Executors.newVirtualThreadPerTaskExecutor();
        }

        HttpProtocolSelector selector = new HttpProtocolSelector();
        HttpProtocolHandlerFactory handlerFactory =
                HttpProtocolHandlerFactory.create(new DefaultServletAdapter(servlet));

        this.connector = new Connector(config.port(), executor, true,selector, handlerFactory);
    }

    @Override
    public void start() throws Exception {
        log.info("[WAS] Initializing server resources (VirtualMode: {})", virtualMode);

        monitor = Executors.newSingleThreadScheduledExecutor();
        monitor.scheduleAtFixedRate(() -> {
            // BIO 모드일 때만 상세 지표 출력 (ThreadPoolExecutor인 경우만 가능)
            if (!virtualMode && executor instanceof ThreadPoolExecutor tpe) {
                log.info("[BIO] active={} queue={} rejected={}",
                        tpe.getActiveCount(),
                        tpe.getQueue().size(),
                        rejectedCount.get()
                );
            } else if (virtualMode) {
                // 가상 스레드는 고정된 풀이 없으므로 단순 상태만 출력
                log.info("[VIRTUAL] Processing requests via Virtual Threads...");
            }
        }, 0, 5, TimeUnit.SECONDS); // 너무 자주 찍히면 정신없으니 5초 정도로 조절 추천

        connector.start();
        log.info("[WAS] Server initialization complete");
    }

    @Override
    public void stop() throws Exception {
        log.info("[WAS] Shutting down server");

        // 1. 새로운 연결 수락 중단
        connector.stop();

        // 2. 실행 중인 작업 완료 대기 및 종료
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 3. 모니터링 종료
        if (monitor != null) {
            monitor.shutdown();
        }

        log.info("[WAS] Server shutdown complete");
    }
}