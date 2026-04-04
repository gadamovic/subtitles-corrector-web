package com.subtitlescorrector.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class LevenshteinDistanceServiceTest {

	@Test
	public void givenStrings_whenCalculatingEditDistance_thenCalculateCorrectly() {
		
		LevenshteinDistanceServiceImpl service = new LevenshteinDistanceServiceImpl();
		assertEquals(0, service.getEditDistance("1", "1"));
		assertEquals(1, service.getEditDistance("1", "11"));
		assertEquals(2, service.getEditDistance("1", "111"));
		assertEquals(1, service.getEditDistance("a", "b"));
	}
	
}
