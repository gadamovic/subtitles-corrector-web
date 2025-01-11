package com.subtitlescorrector.service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.subtitlescorrector.service.redis.RedisService;
import com.subtitlescorrector.util.Constants;

@Service
public class WebSocketUserServiceImpl implements WebSocketUserService {

	Logger log = LoggerFactory.getLogger(WebSocketUserServiceImpl.class);

	@Autowired
	RedisService redisService;

	@Override
	public void processUserRegistrationIfNeeded(String message, WebSocketSession session) {

		Pattern pattern = Pattern.compile(".*<(.*)>.*");
		Matcher matcher = pattern.matcher(message);

		String userId = null;

		try {
			if (matcher.find()) {
				userId = matcher.group(1);

				redisService.addWebSocketUserToCache(userId, session.getId());
				log.info("Registered websocket user: " + userId);
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(Constants.WEB_SOCKET_USER_ACK_SIGNAL));
				}
			}
		} catch (Exception e) {
			log.error("Error processing user registration!", e);
		}
	}

	@Override
	public WebSocketSession findUserSession(String webSocketSessionId, List<WebSocketSession> sessions) {

		WebSocketSession foundSession = sessions.stream().filter(session -> session.getId().equals(webSocketSessionId))
				.findAny().orElse(null);

		return foundSession;
	}

}
