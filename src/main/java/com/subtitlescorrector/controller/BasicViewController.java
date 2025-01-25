package com.subtitlescorrector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.subtitlescorrector.service.EmailServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

//TODO: Implement Filter instead of controller for logging users

@Controller
public class BasicViewController {
	
	Logger log = LoggerFactory.getLogger(BasicViewController.class);
	
	@RequestMapping(path="/")
	public String home(HttpServletRequest request) {
		
		log.info("Client ip: " + request.getRemoteAddr() + ". Request url: " + request.getRequestURL().toString());
		return "index";
		
	}
	
	//for all return index because of client side rendering
	@RequestMapping(path="terms-of-service")
	public String termsOfService(HttpServletRequest request) {
		log.info("terms-of-service");
		return "index";
		
	}
	
	@RequestMapping(path="privacy-policy")
	public String privacyPolicy(HttpServletRequest request) {
		log.info("privacy-policy");
		return "index";
		
	}
}


