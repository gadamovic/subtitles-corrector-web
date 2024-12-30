package com.subtitlescorrector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageBrokerService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }
	
    public void sendNotificationToUser(String user, String destination, String message) {
        messagingTemplate.convertAndSendToUser(user, destination, message);
    }
    
}
