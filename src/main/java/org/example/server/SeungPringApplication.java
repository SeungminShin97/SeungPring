package org.example.server;

import org.example.framework.context.MyApplicationContext;
import org.example.framework.core.ConfigurableApplicationContext;
import org.example.framework.was.container.Servlet;
import org.example.server.config.SeungPringApplicationConfig;
import org.example.server.servlet.ServletInfrastructure;
import org.example.server.was.WasInfrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SeungPring 프레임워크의 최상위 실행 진입점.
 *
 * <p>
 * IoC 컨테이너 초기화, Servlet 실행 구조 조립,
 * WAS 서버 구동까지의 전체 생명주기를 총괄한다.
 * </p>
 */
public class SeungPringApplication {

    private static final Logger log = LoggerFactory.getLogger(SeungPringApplication.class);

    private ConfigurableApplicationContext context;
    private WasInfrastructure was;

    public void run(Class<?> source, String[] args) {
        try {
            // 1. 옵션 구성
            SeungPringApplicationConfig config =
                    SeungPringApplicationConfig.from(source, args);

            log.info("[Application] Starting SeungPring");

            // 2. IoC 초기화
            this.context = new MyApplicationContext(config.basePackages());
            context.start();

            log.info("[Application] ApplicationContext initialized");

            // 3. Servlet 구성
            ServletInfrastructure servletInfra =
                    new ServletInfrastructure(context);

            Servlet servlet = servletInfra.servlet();

            // 4. WAS 구성
            this.was = new WasInfrastructure(config, servlet);

            // 5. shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    log.info("[Application] Shutdown hook triggered");
                    was.stop();
                    context.stop();
                } catch (Exception e) {
                    log.error("[Application] Error during shutdown", e);
                }
            }, "SeungPring-ShutdownHook"));

            // 6. 서버 시작
            was.start();
            log.info("[Application] SeungPring started successfully");

        } catch (Exception e) {
            log.error("[Application] Failed to start SeungPring", e);
            throw new IllegalStateException("Failed to start SeungPring", e);
        }
    }
}
