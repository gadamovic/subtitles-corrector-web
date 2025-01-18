package com.subtitlescorrector.controller.rest;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;

@RestController
public class CertbotChallengeController {

	@Autowired
	ApplicationProperties properties;
	
	Logger log = LoggerFactory.getLogger(CertbotChallengeController.class);
	
	@RequestMapping(path = "/.well-known/acme-challenge/{filename}")
	public ResponseEntity<Resource> getChallengeFiles(@PathVariable(name = "filename") String filename) {
		
		//TODO: Try to enable TLS for https://13.61.96.166/
		log.info("Certbot controller executing with filename: " + filename);
		
		File file = new File(properties.getCertbotChallengePath(), filename);
        if (file.exists()) {
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
		
	}
	
}
