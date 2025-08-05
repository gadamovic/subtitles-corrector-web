package com.subtitlescorrector.adapters.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	Logger log = LoggerFactory.getLogger(WebSocketController.class);
	
	@MessageMapping("/ws/1.0/upload")
	public void foo(String msg) {
		log.info("Message from websocket: " + msg);
	}
	
}
