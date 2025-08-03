package com.subtitlescorrector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class BasicViewController {
	
	//TODO: Add checkbox to choose html tags stripping
	
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
	
	@RequestMapping(path="converters")
	public String converters(HttpServletRequest request) {
		log.info("converters");
		return "index";
		
	}
}


