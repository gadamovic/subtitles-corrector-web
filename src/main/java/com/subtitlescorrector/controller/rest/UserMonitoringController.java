package com.subtitlescorrector.controller.rest;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.domain.LogUserDataHolder;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/rest/1.0")
public class UserMonitoringController {

	Logger log = LoggerFactory.getLogger(UserMonitoringController.class);
	
	@RequestMapping(path = "/logUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void logUser(@RequestBody LogUserDataHolder data, HttpServletRequest request) {
		log.info(data.toString());
		
		Collections.list(request.getHeaderNames())
	    .forEach(name -> log.info(name + ": " + request.getHeader(name)));
		
	}

}
