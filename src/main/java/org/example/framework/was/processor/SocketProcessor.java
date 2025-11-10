package org.example.framework.was.processor;

import java.io.IOException;
import java.net.Socket;

public class SocketProcessor implements Runnable{

    private final Socket socket;

    public SocketProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (socket) {
            // 요청 처리
            System.out.println("asdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
