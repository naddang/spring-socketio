package com.dev_cbj.springsocketio.socket.config;

import com.dev_cbj.springsocketio.util.func.StringUtil;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoServerOptions;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoSocket;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {
	
	@Bean
	EngineIoServer engineIoServer() {
		var opt = EngineIoServerOptions.newFromDefault();
		opt.setCorsHandlingDisabled(true);
		return new EngineIoServer(opt);
	}
	
	@Bean
	SocketIoServer socketIoServer(EngineIoServer eioServer) {
		var sioServer = new SocketIoServer(eioServer);
		
		var namespace = sioServer.namespace("/ws");
		
		namespace.on("connection", args -> {
			var socket = (SocketIoSocket) args[0];
			
			socket.send("public", "Socket Connected!");
			
			socket.on("message", args1 -> {
				JSONObject o = (JSONObject) args1[0];
				var messageVo = StringUtil.toPojoObj(o, MessageVo.class);
				
				socket.send("hello",
						StringUtil.toJsonObj(
								new MessageVo("Server", "Hello, " + messageVo.author + "!")));
			});
		});
		
		return sioServer;
	}
	
	record MessageVo(String author, String msg) {}
}