package com.subtitlescorrector.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.subtitlescorrector.adapters.out.configuration.WebSocketBrokerAvailabilityListener;
import com.subtitlescorrector.core.util.Util;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

@Service
@Deprecated
public class WebSocketMessageBrokerAdapter {

	Logger log = LoggerFactory.getLogger(WebSocketMessageBrokerAdapter.class);
	
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
