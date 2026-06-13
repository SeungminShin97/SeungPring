톰캣에서 **소켓 여는 부분(accept loop)** 은 `org.apache.coyote` 패키지 아래에 있다.

정확히는 **`org.apache.coyote.http11.Http11NioProtocol` → `org.apache.tomcat.util.net.NioEndpoint`** 가 핵심이다.

구조 요약:

```
Connector
 └── ProtocolHandler (Http11NioProtocol)
       └── Endpoint (NioEndpoint)
             └── Acceptor (스레드)
                  └── SocketProcessor (요청 처리)

```

- **`Connector`**

  → 포트, 프로토콜 설정만 관리 (예: `8080`, `HTTP/1.1` 등)

  → 실제 소켓 열지 않음. 대신 `ProtocolHandler`를 생성하고 초기화.

- **`Http11NioProtocol` (ProtocolHandler 구현체)**

  → `NioEndpoint`를 만들어 실제 I/O 처리 담당.

  → `start()` 시 내부에서 `endpoint.startInternal()` 호출.

- **`NioEndpoint`**

  → 진짜로 `ServerSocketChannel` 열고 `Selector` 등록함.

  → 내부 `Acceptor` 스레드가 `accept()` 루프 돌며 클라이언트 소켓 수락.

  → 각 소켓은 `SocketProcessor`가 스레드풀에서 처리.


참고 클래스 파일 경로 (톰캣 10.x 기준):

```
tomcat-embed-core-10.x.x.jar
└── org/apache/tomcat/util/net/NioEndpoint.java
└── org/apache/coyote/http11/Http11NioProtocol.java
└── org/apache/coyote/AbstractProtocol.java

```

GitHub에서 직접 볼 수 있는 위치:

https://github.com/apache/tomcat/tree/main/java/org/apache/tomcat/util/net

→ 여기서 `NioEndpoint.java`가 소켓 여는 핵심이다.

---

너가 “승프링”에서 **소켓 열기부터 Connector 구현까지 따라 하고 싶다면**,

다음 순서로 진행하는 게 이상적이다:

1. `ServerSocketChannel` 열고 `accept()` 루프 만드는 최소 구조.
2. accept된 `SocketChannel`을 `RequestHandler`로 넘겨 I/O 처리.
3. 이후 `Connector` 클래스를 만들어 포트, 프로토콜 설정 캡슐화.
4. 마지막에 `DispatcherServlet` 이전 단계로 연결.

```dbn-psql
org.example.framework.was
├── connector
│   └── MyConnector.java
│
├── endpoint
│   ├── AbstractEndpoint.java
│   ├── MyBioEndpoint.java
│   └── MyNioEndpoint.java
│
├── protocol
│   ├── MyHttpProtocolHandler.java
│   └── RequestParser.java          # (선택) HTTP 요청 파싱용
│
├── processor
│   ├── SocketProcessor.java
│   └── Acceptor.java
│
└── server
    └── MyWebServer.java            # (선택) Connector 관리 및 전체 부팅 담당

```