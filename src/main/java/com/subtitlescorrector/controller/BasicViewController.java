package com.subtitlescorrector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.subtitlescorrector.service.EmailServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BasicViewController {
	
	Logger log = LoggerFactory.getLogger(BasicViewController.class);
	
	@RequestMapping(path="/")
	public String upload(HttpServletRequest request) {
		
		log.info("Client ip: " + request.getRemoteAddr());
		return "index";
		
	}
}


