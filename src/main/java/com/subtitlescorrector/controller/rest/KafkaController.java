package com.subtitlescorrector.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.consumers.SubtitleCorrectionEventConsumer;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class KafkaController {

	@Autowired
	SubtitleCorrectionEventConsumer consumer;
	
	@RequestMapping(path = "/startConsumingCorrections")
	public void startConsumingCorrections() {
		consumer.consumeCorrections();
	}
	
}
