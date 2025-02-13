package com.subtitlescorrector;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import static com.subtitlescorrector.util.Util.generateS3Key;

public class UtilTests {

	@Test
	void testGenerateS3Key(){
		
		String generated = generateS3Key("postfix");
		assertTrue(generated.length() > 0);
		assertTrue(generated.endsWith("postfix"));
		
	}
	
}
