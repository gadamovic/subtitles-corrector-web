package com.subtitlescorrector.core.service.websocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.port.RedisServicePort;

@Service
public class WebSocketUserRegistrationServiceImpl implements WebSocketUserRegistrationService {

	@Autowired
	RedisServicePort redisService;

	Logger log = LoggerFactory.getLogger(WebSocketUserRegistrationServiceImpl.class);

	@Override
	public boolean registerUser(String sessionId, String payload) {

		try {
			Pattern pattern = Pattern.compile(".*<(.*)>.*");
			Matcher matcher = pattern.matcher(payload);

			String userId = null;

			if (matcher.find()) {
				userId = matcher.group(1);
				redisService.addWebSocketUserToCache(userId, sessionId);
				log.info("Registered websocket user: " + userId);
			}

			log.info("Websocket message: " + payload);

			return true;

		} catch (Exception e) {
			log.error("Error persisting WebSocket session id!", e);
			return false;
		}
	}

}
