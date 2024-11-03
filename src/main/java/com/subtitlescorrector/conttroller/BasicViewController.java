package com.subtitlescorrector.conttroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BasicViewController {
	
	@RequestMapping(path="/test")
	public String test() {
	
		return "index";
		
	}
	
	@RequestMapping(path="/upload")
	public String upload() {
	
		return "upload";
		
	}
}


