package com.subtitlescorrector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BasicViewController {
	
	@RequestMapping(path="/test")
	public String test() {
	
		return "index";
		
	}
	
	@RequestMapping(path="/")
	public String upload() {
	
		return "upload";
		
	}
}


