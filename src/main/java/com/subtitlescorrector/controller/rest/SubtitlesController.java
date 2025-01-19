package com.subtitlescorrector.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.domain.CompositeEditOperation;
import com.subtitlescorrector.domain.EditOperation;
import com.subtitlescorrector.service.subtitles.EditDistanceService;
import com.subtitlescorrector.util.Util;

@RestController
@RequestMapping("/api/rest/1.0")
public class SubtitlesController {

	@Autowired
	EditDistanceService levenshteinDistance;
	
	@RequestMapping("/getStringDifferences")
	public List<CompositeEditOperation> getStringDifferences(@RequestParam("s1") String s1, @RequestParam("s2") String s2){
	
		List<EditOperation> editOperations = levenshteinDistance.getEditOperations(s1, s2);
		List<CompositeEditOperation> compEditOperations = Util.convertToCompositeEditOperations(editOperations);
		
		return compEditOperations;
	}
	
}
