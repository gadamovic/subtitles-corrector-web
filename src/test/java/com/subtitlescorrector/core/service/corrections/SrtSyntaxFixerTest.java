package com.subtitlescorrector.core.service.corrections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.service.corrections.srt.SrtSyntaxFixerImpl;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class SrtSyntaxFixerTest {
	
	@Mock
	WebSocketMessageSender webSocketMessageSender;
	
	@InjectMocks
	SrtSyntaxFixerImpl fixer;
	
	@BeforeEach
	public void setup() {
		
		doNothing().when(webSocketMessageSender).sendMessage(any(SubtitleCorrectionEvent.class));
		
	}
	
	@Test
	public void givenLineOfText_whenValidatingAndFixing_fixInvalidTimestamp(){

		String before = "00:02:30,530 ---> 00:02:32,019";
		String after = fixer.validateAndFixTimestampLine(before);
		
		assertEquals("00:02:30,530 --> 00:02:32,019", after);
		
		before = "00:02:30,530-->00:02:32,019";
		after = fixer.validateAndFixTimestampLine(before);
		
		assertEquals("00:02:30,530 --> 00:02:32,019", after);
		
		before = "00:02:30,5 --> 00:02:3,019";
		after = fixer.validateAndFixTimestampLine(before);
		
		assertEquals("00:02:30,500 --> 00:02:30,019", after);
		
		before = "00:47:24,737 --> 00:47:26,970";
		after = fixer.validateAndFixTimestampLine(before);
		
		assertEquals("00:47:24,737 --> 00:47:26,970", after);
		
	}
	
}
