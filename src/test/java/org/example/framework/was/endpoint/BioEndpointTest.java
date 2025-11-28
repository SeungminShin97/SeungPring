package org.example.framework.was.endpoint;

import org.example.framework.was.processor.SocketProcessor;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BioEndpointTest {

    private final int TEST_PORT = 8081;

    private ExecutorService executor;
    private HttpProtocolSelector selector;
    private HttpProtocolHandlerFactory factory;

    private BioEndpoint bioEndpoint;

    @BeforeEach
    void setUp() {
        executor = mock(ExecutorService.class);
        selector = mock(HttpProtocolSelector.class);
        factory = mock(HttpProtocolHandlerFactory.class);

        bioEndpoint = new BioEndpoint(TEST_PORT, executor, selector, factory);
    }

    @AfterEach
    void tearDown() throws IOException {
        if(bioEndpoint.isRunning())
            bioEndpoint.stop();
    }

    @Test
    @DisplayName("start() 호출 후 클라이언트 연결 시 SocketProcessor가 Executor에 제출되어야 한다.")
    void should_execute_SocketProcessor_when_client_connects_after_start() throws IOException, InterruptedException {
        // === given ===
        // ExecutorService에 제출되는 SocketProcessor 캡처
        ArgumentCaptor<SocketProcessor> captor = ArgumentCaptor.forClass(SocketProcessor.class);

        // === when ===
        bioEndpoint.start();

        // === then ===
        assertTrue(bioEndpoint.isRunning());

        // 소캣을 만들어 BIO에 접속
        try(Socket clientSocket = new Socket("Localhost", TEST_PORT)) {
            assertTrue(clientSocket.isConnected());

            Thread.sleep(200);
        }

        // execute가 1번 호출 됐는지
        verify(executor, times(1)).execute(captor.capture());
        // SocketProcessor인지 확인
        SocketProcessor capturedProcessor = captor.getValue();
        assertNotNull(capturedProcessor);
        assertInstanceOf(SocketProcessor.class, capturedProcessor);

        bioEndpoint.stop();
        assertFalse(bioEndpoint.isRunning());
    }

    @Test
    @DisplayName("stop() 호출 후 요청 처리 및 Executor 제출이 발생하지 않아야 한다")
    void should_not_submit_processor_when_server_is_stopped() throws IOException, InterruptedException {
        // === given ===
        bioEndpoint.start();
        Thread.sleep(200);

        // === when ===
        bioEndpoint.stop();

        // === then ===
        verify(executor, never()).execute(any());
        assertFalse(bioEndpoint.isRunning());
    }
}