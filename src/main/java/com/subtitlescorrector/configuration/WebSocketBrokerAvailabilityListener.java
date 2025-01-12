package com.subtitlescorrector.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.service.WebSocketMessageBrokerService;

@Component
public class WebSocketBrokerAvailabilityListener {

	Logger log = LoggerFactory.getLogger(WebSocketBrokerAvailabilityListener.class);
	
    private boolean brokerAvailable = false;

    @EventListener
    public void onBrokerAvailabilityEvent(BrokerAvailabilityEvent event) {
        brokerAvailable = event.isBrokerAvailable();
        if (brokerAvailable) {
        	log.info("Broker is now available!");
        } else {
        	log.info("Broker is unavailable!");
        }
    }

    public boolean isBrokerAvailable() {
        return brokerAvailable;
    }
}
