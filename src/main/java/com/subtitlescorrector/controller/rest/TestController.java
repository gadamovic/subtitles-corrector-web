package com.subtitlescorrector.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.domain.openai.ChatResponse;
import com.subtitlescorrector.service.ai.AiServiceImpl;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class TestController {

	Logger log = LoggerFactory.getLogger(TestController.class);

	@Autowired
	AiServiceImpl aiService;
	
	@RequestMapping(path = "/ai/test")
	public void testAI() {
		ChatResponse response = aiService.askOpenAi("This is api test.");
		log.info(response.getFirstChoiceMessage());
	}
	
}
