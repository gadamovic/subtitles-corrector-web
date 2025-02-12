package com.subtitlescorrector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.subtitlescorrector.configuration.WebSocketBrokerAvailabilityListener;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.util.Util;

@Service
@Deprecated
public class WebSocketMessageBrokerService {

	Logger log = LoggerFactory.getLogger(WebSocketMessageBrokerService.class);
	
    //@Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    //@Autowired
    private WebSocketBrokerAvailabilityListener brokerAvailabilityListener;
    

    public void sendNotification(String destination, String message) {
    	messagingTemplate.convertAndSend(destination, message);
    }


    public void sendNotificationToUser(String user, String destination, String message) {
        messagingTemplate.convertAndSendToUser(user, destination, message);
    }
    
    public void sendSubtitleCorrectionEventToUser(SubtitleCorrectionEvent event) {
    	if(event.getWebSocketSessionId() != null) {
    		sendNotificationToUser(event.getWebSocketSessionId().toString(), "/subtitles-processing-log", Util.subtitleCorrectionEventToJson(event));
    	}
    }
    
    
}
