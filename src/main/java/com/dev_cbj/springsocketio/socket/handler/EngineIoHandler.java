package com.dev_cbj.springsocketio.socket.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoWebSocket;
import io.socket.engineio.server.utils.ParseQS;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Socket.IO 핸들러 클래스<br/>
 * EngineIoServer를 통해 소켓을 처리하게끔 함<br/>
 * <a href="https://medium.com/@huyvu8051/setup-project-spring-boot-2-x-socket-io-client-4-x-cf10e0f86bbb">링크</a>를 참고하였음
 */
@Controller
@AllArgsConstructor
@Slf4j
public class EngineIoHandler implements HandshakeInterceptor, org.springframework.web.socket.WebSocketHandler {
	private static final String ATTRIBUTE_ENGINE_IO_BRIDGE = "engine.io.bridge";
	private static final String ATTRIBUTE_ENGINE_IO_QUERY = "engine.io.query";
	private static final String ATTRIBUTE_ENGINE_IO_HEADERS = "engine.io.headers";
	
	private static final ConcurrentHashMap<String, WebSocketSession> MAP = new ConcurrentHashMap<>(); // 웹소켓 세션을 저장하는 맵
	private final EngineIoServer mEngineIoServer;

	/**
	 * Socket.IO의 폴링 요청을 처리하는 핸들러
	 * 폴링 요청 시 EnginIoServer가 알아서 처리하게끔 함
	 * @param request 요청객체
	 * @param response 응답객체
	 * @throws IOException 입출력 예외
	 */
	@RequestMapping(
			value = {"/socket.io/", "/socket.io."},
			method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
			headers = "Connection!=Upgrade")
	public void httpHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
		mEngineIoServer.handleRequest(request, response);
	}
	
	/* 이하는 HandshakeInterceptor 를 override 한 부분*/
	/**
	 * 웹소켓 연결 전에 호출되는 메소드. IP 검사 등을 여기서 처리하면 됨.
	 * @param request 요청객체
	 * @param response 응답객체
	 * @param wsHandler 웹소켓 핸들러
	 * @param attributes 웹소켓 세션에 데이터를 담을 맵
	 * @return true: 연결 허용, false: 연결 거부
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
	                               org.springframework.web.socket.WebSocketHandler wsHandler,
	                               Map<String, Object> attributes) {
		attributes.put(ATTRIBUTE_ENGINE_IO_QUERY, request.getURI().getQuery());
		attributes.put(ATTRIBUTE_ENGINE_IO_HEADERS, request.getHeaders());
		return true;
	}
	
	/**
	 * 핸드셰이크 후 호출되는 메소드.
	 * @param request 요청객체
	 * @param response 응답객체
	 * @param wsHandler 웹소켓핸들러
	 * @param exception 예외
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
	                           org.springframework.web.socket.WebSocketHandler wsHandler,
	                           Exception exception) {
		//todo
	}
	
	/* 이하는 WebSocketHandler override 한 부분 */
	
	/**
	 * 메시지 분할 가능 여부 설정하는 메소드.
	 * @return true: 메시지를 분할해서 받을 수 있음, false: 메시지를 분할해서 받을 수 없음
	 */
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
	
	/**
	 * 웹소켓 연결이 열리면 호출되는 메소드. 유효성 검사 통과여부 등을 처리함.
	 * @param webSocketSession 웹소켓 세션
	 */
	@Override
	public void afterConnectionEstablished(@NonNull WebSocketSession webSocketSession) {
		final EngineIoSpringWebSocket webSocket = new EngineIoSpringWebSocket(webSocketSession);
		webSocketSession.getAttributes().put(ATTRIBUTE_ENGINE_IO_BRIDGE, webSocket);
		mEngineIoServer.handleWebSocket(webSocket);
		if (MAP.get(webSocketSession.getId()) == null) MAP.put(webSocketSession.getId(), webSocketSession);
	}
	
	/**
	 * 웹소켓 연결이 닫히면 호출되는 메소드.
	 * @param webSocketSession 웹소켓 세션
	 * @param closeStatus 클로즈 상태
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, @NonNull CloseStatus closeStatus) {
		((EngineIoSpringWebSocket)webSocketSession.getAttributes().get(ATTRIBUTE_ENGINE_IO_BRIDGE))
				.afterConnectionClosed(closeStatus);
		MAP.remove(webSocketSession.getId());
	}
	
	/**
	 * 웹소켓 메시지가 도착하면 호출되는 메소드.
	 * @param session 웹소켓 세션
	 * @param message 웹소켓 메시지
	 */
	@Override
	public void handleMessage(WebSocketSession session, @NonNull WebSocketMessage<?> message) throws IOException {
		// 메시지 파싱
		String[] result = parseSocketIoMessage(message);
		String payload = (String) message.getPayload();

		//socketIO 기본 메시지는 무시
		if ("2probe".equals(payload) || "5".equals(payload) || "3".equals(payload)) {
			((EngineIoSpringWebSocket)session.getAttributes().get(ATTRIBUTE_ENGINE_IO_BRIDGE))
					.handleMessage(message);
			return;
		}

		// if: 이벤트명이 public인 경우, else: 이벤트명이 private인 경우
		if ("public".equals(result[0])) {
			log.info("public event: {}", result[1]);
			//모든 클라이언트에게 데이터 전송
			sendDataToWebSocket("User " + session.getId() + "'s Message: " + result[1]);
		} else {
			log.info("private event: {}", result[1]);
			//해당 이벤트를 구독하고 있는 클라이언트에게 데이터 전송
			session.sendMessage(new TextMessage(
					toSocketIoMessage("message",
							"User " + session.getId() + "! Server receive Your Message!" +
							"Message Was: " + result[1])
			));
		}

		((EngineIoSpringWebSocket)session.getAttributes().get(ATTRIBUTE_ENGINE_IO_BRIDGE))
				.handleMessage(message);
	}
	
	/**
	 * 웹소켓 전송 에러 발생 시 호출되는 메소드.
	 * @param webSocketSession 웹소켓 세션
	 * @param throwable 예외
	 */
	@Override
	public void handleTransportError(WebSocketSession webSocketSession, @NonNull Throwable throwable) {
		((EngineIoSpringWebSocket)webSocketSession.getAttributes().get(ATTRIBUTE_ENGINE_IO_BRIDGE))
				.handleTransportError(throwable);
	}
	
	/**
	 * 클라이언트사이드 웹소켓에 데이터를 전송하는 메소드.<br/>
	 * 모든 클라이언트에게 데이터를 전송함.(클라이언트는 <b>public</b> 이벤트를 구독하고 있어야함)
	 * @param data 전송할 데이터
	 */
	public void sendDataToWebSocket(Object data) {
		MAP.forEach((key, value) -> {
			sendDataToWebSocket("public", data);
		});
	}
	
	/**
	 * 클라이언트사이드 웹소켓에 데이터를 전송하는 메소드.<br/>
	 * 해당 이벤트를 구독하고있는 클라이언트에게 데이터를 전송함.
	 * @param eventName 이벤트 이름
	 * @param data 전송할 데이터
	 */
	public void sendDataToWebSocket(String eventName, Object data) {
		MAP.forEach((key, value) -> {
			try {
				var socket = (EngineIoSpringWebSocket)value.getAttributes().get(ATTRIBUTE_ENGINE_IO_BRIDGE);
				socket.write(toSocketIoMessage(eventName, data));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * 클라이언트사이드 웹소켓에 데이터를 전송하는 메소드.<br/>
	 * 모든 클라이언트에게 데이터를 전송함.(클라이언트는 <b>public</b> 이벤트를 구독하고 있어야함)
	 * @param data 전송할 데이터
	 */
	public void sendDataToWebSocket(Map<String, Object> data) {
		MAP.forEach((key, value) -> {
			sendDataToWebSocket("public", data);
		});
	}
	
	/**
	 * 클라이언트사이드 웹소켓에 데이터를 전송하는 메소드.<br/>
	 * 해당 이벤트를 구독하고있는 클라이언트에게 데이터를 전송함.
	 * @param eventName 이벤트 이름
	 * @param data 전송할 데이터
	 */
	public void sendDataToWebSocket(String eventName, Map<String, Object> data) {
		MAP.forEach((key, value) -> {
			try {
				var socket = (EngineIoSpringWebSocket)value.getAttributes().get(ATTRIBUTE_ENGINE_IO_BRIDGE);
				socket.write(toSocketIoMessage(eventName, data));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Socket IO의 형식으로 객체를 메시지로 변환하는 메서드.<br/>
	 * SocketIO로 메시지 전송 시 "{42/ws,[eventName, data]}" 형식으로 전송해야함.
	 * @param eventName 이벤트 이름
	 * @param data 전송할 데이터
	 * @return 소켓IO 메시지
	 */
	private String toSocketIoMessage(String eventName, Object data) {
		String result = "42/ws,";
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(eventName);
		jsonArray.add(new Gson().toJsonTree(data));
		return result + jsonArray;
	}
	
	/**
	 * EngineIoWebSocket 클래스.<br/>
	 * Socket.IO의 폴링과 같은 요청들을 처리하는 클래스
	 */
	private static final class EngineIoSpringWebSocket extends EngineIoWebSocket {
		private final WebSocketSession mSession;
		private final Map<String, String> mQuery;
		private final Map<String, List<String>> mHeaders;
		
		EngineIoSpringWebSocket(WebSocketSession session) {
			mSession = session;
			
			final String queryString = (String)mSession.getAttributes().get(ATTRIBUTE_ENGINE_IO_QUERY);
			if (queryString != null) {
				mQuery = ParseQS.decode(queryString);
			} else {
				mQuery = new HashMap<>();
			}
			this.mHeaders = (Map<String, List<String>>) mSession.getAttributes().get(ATTRIBUTE_ENGINE_IO_HEADERS);
		}
		
		/* EngineIoWebSocket */
		
		@Override
		public Map<String, String> getQuery() {
			return mQuery;
		}
		
		@Override
		public Map<String, List<String>> getConnectionHeaders() {
			return mHeaders;
		}
		
		@Override
		public void write(String message) throws IOException {
			mSession.sendMessage(new TextMessage(message));
		}
		
		@Override
		public void write(byte[] message) throws IOException {
			mSession.sendMessage(new BinaryMessage(message));
		}
		
		@Override
		public void close() {
			try {
				mSession.close();
			} catch (IOException ignore) {}
		}
		
		/* WebSocketHandler */
		
		void afterConnectionClosed(CloseStatus closeStatus) {
			emit("close");
		}
		
		void handleMessage(WebSocketMessage<?> message) {
			if (message.getPayload() instanceof String || message.getPayload() instanceof byte[]) {
				emit("message", message.getPayload());
			} else {
				throw new RuntimeException(String.format(
						"Invalid message type received: %s. Expected String or byte[].",
						message.getPayload().getClass().getName()));
			}
		}
		
		void handleTransportError(Throwable exception) {
			emit("error", "write error", exception.getMessage());
		}
	}

	/**
	 * @param message socketIO 메시지를 파싱함
	 * socketIO 메시지는 42/{url},["이벤트명", 데이터]의 형식임
	 * @return result[0] : 이벤트 이름, result[1] : 데이터
	 */
	private String[] parseSocketIoMessage(WebSocketMessage message) {
		String[] result = new String[2];
		String payload = (String) message.getPayload();
		if (!payload.startsWith("42/ws")) return result;
		
		payload = payload.replace("42/ws,", ""); // 42/ws, 를 제거함
		
		JsonArray jsonArray = JsonParser.parseString(payload).getAsJsonArray();
		
		result[0] = jsonArray.get(0).getAsString(); // 이벤트명
		if (jsonArray.get(1).isJsonObject()) result[1] = jsonArray.get(1).toString(); // accessToken
		else result[1] = jsonArray.get(1).getAsString(); // 데이터
		
		return result;
	}
}