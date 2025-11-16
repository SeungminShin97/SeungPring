package org.example.framework.was.processor;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketProcessor implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(SocketProcessor.class);

    private final Socket socket;
    private final HttpProtocolSelector selector;
    private final HttpProtocolHandlerFactory handlerFactory;

    public SocketProcessor(Socket socket, HttpProtocolSelector selector, HttpProtocolHandlerFactory handlerFactory) {
        this.socket = socket;
        this.selector = selector;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public void run() {
        try (socket) {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            HttpProtocolVersion version = selector.detect(in);
            HttpProtocolHandler handler = handlerFactory.getHandler(version);

            handler.process(in, out);
        } catch (IOException e) {
            log.error("[SocketProcessor] Socket I/O Error, connection closed by client/network: {}", e.getMessage());
        } catch (HttpParsingException e) {
            // TODO: 400 에러 발송 후 소켓종료 로직 추가
        } catch (Exception e) {
            // TODO: 500 예외 발송??
            throw new RuntimeException(e);
        }
    }
}