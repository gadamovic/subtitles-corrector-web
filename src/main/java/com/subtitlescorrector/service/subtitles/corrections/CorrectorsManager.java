package com.subtitlescorrector.service.subtitles.corrections;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
