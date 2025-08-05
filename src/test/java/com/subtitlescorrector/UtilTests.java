package com.subtitlescorrector;

import static com.subtitlescorrector.core.util.Util.generateS3Key;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UtilTests {

	@Test
	void testGenerateS3Key(){
		
		String generated = generateS3Key("postfix");
		assertTrue(generated.length() > 0);
		assertTrue(generated.endsWith("postfix"));
		
	}
	
}
