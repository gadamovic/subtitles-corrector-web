package com.subtitlescorrector.adapters.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;


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
	
	@RequestMapping(path="converters")
	public String converters(HttpServletRequest request) {
		log.info("converters");
		return "index";
		
	}
	
	@RequestMapping(path="to-srt-converter")
	public String toSrtConverter(HttpServletRequest request) {
		log.info("to-srt-converter");
		return "index";
		
	}

	@RequestMapping(path="to-vtt-converter")
	public String toVttConverter(HttpServletRequest request) {
		log.info("to-vtt-converter");
		return "index";
		
	}

	@RequestMapping(path="to-ass-converter")
	public String toAssConverter(HttpServletRequest request) {
		log.info("to-ass-converter");
		return "index";
		
	}
	
	@RequestMapping(path="translate-subtitles")
	public String translateSubtitles(HttpServletRequest request) {
		log.info("translate-subtitles");
		return "index";	
	}
	
}


