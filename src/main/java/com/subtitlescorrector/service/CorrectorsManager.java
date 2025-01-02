package com.subtitlescorrector.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.service.subtitles.corrections.Corrector;
import com.subtitlescorrector.service.subtitles.corrections.InvalidCharactersCorrector;

@Service
public class CorrectorsManager {

	@Autowired
	InvalidCharactersCorrector invalidCharactersCorrector;
	
	public List<Corrector> getCorrectors() {
		
		List<Corrector> correctors = new ArrayList<>();
		correctors.add(invalidCharactersCorrector);
		
		return correctors;
		
	}
	
}
