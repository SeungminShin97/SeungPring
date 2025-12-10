package org.example.framework.was.server;

import org.example.framework.was.connector.Connector;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLauncher {
    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        HttpProtocolSelector selector = new HttpProtocolSelector();
        HttpProtocolHandlerFactory handlerFactory = HttpProtocolHandlerFactory.getInstance();

        Connector connector = new Connector(port, executor, selector, handlerFactory);

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
