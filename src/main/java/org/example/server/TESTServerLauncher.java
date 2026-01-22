package org.example.server;

import org.example.framework.was.adapter.DefaultServletAdapter;
import org.example.framework.was.connector.Connector;
import org.example.framework.was.container.ServletContainer;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.example.framework.was.server.Service;
import org.example.framework.web.DispatcherServlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 테스트 용 런처
 */
public class TESTServerLauncher {
    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        HttpProtocolSelector selector = new HttpProtocolSelector();
        HttpProtocolHandlerFactory handlerFactory = HttpProtocolHandlerFactory.create(new DefaultServletAdapter(null));
        Service service = new Service(new ServletContainer(new DispatcherServlet(null, null)));

        Connector connector = new Connector(port, executor, selector, handlerFactory, service);

        // 종료 훅 추가
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            connector.stop();
            executor.shutdown();
        }));

        connector.start();
        System.out.println("Server running on port " + port);

        Thread.currentThread().join();
    }
}
