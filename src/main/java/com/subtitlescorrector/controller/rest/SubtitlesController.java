package com.subtitlescorrector.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.CompositeEditOperation;
import com.subtitlescorrector.domain.EditOperation;
import com.subtitlescorrector.service.EmailService;
import com.subtitlescorrector.service.subtitles.EditDistanceService;
import com.subtitlescorrector.util.Util;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/rest/1.0")
public class SubtitlesController {

	@Autowired
	EditDistanceService levenshteinDistance;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	ApplicationProperties properties;
	
	
	@RequestMapping("/getStringDifferences")
	public List<CompositeEditOperation> getStringDifferences(@RequestParam("s1") String s1, @RequestParam("s2") String s2){
	
		List<EditOperation> editOperations = levenshteinDistance.getEditOperations(s1, s2);
		List<CompositeEditOperation> compEditOperations = Util.convertToCompositeEditOperations(editOperations);
		
		return compEditOperations;
	}
	
	@RequestMapping("/cookiesAllowed")
	public void cookiesAllowed(HttpServletRequest request){
	
		emailService.sendEmailOnlyIfProduction("Cookies allowed by: " + request.getRemoteAddr(), properties.getAdminEmailAddress(), "Cookies allowed");

	}
	
	@RequestMapping("/cookiesDeclined")
	public void cookiesDeclined(HttpServletRequest request){
		
		emailService.sendEmailOnlyIfProduction("Cookies denied by: " + request.getRemoteAddr(), properties.getAdminEmailAddress(), "Cookies declined");
	}
	
}
