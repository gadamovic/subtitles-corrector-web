package com.subtitlescorrector.core.service.corrections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.util.Util;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class InvalidCyrillicDetectionTest {

	@Test
	public void givenLinesOfSubtitleText_whenDetectInvalidCyrillic_thenDetectCorrectly(){
		
		// This line has both è and æ meaning it's broken Cyrillic
		String lineWithInvalidCyrillic = "tako soènu i slatku. \n Kako drveæe šumi.";
		List<String> list = new ArrayList<>();
		list.add(lineWithInvalidCyrillic);
		
		UserData user = new UserData();
		
		assertFalse(user.isHasInvalidCyrillic());
		Util.setInvalidCyrillicFlag(list, user);
		assertTrue(user.isHasInvalidCyrillic());
		
		// This line has only è which means that it can be Italian or French
		String lineWithoutInvalidCyrillic = "tako soènu i slatku";
		list.clear();
		list.add(lineWithoutInvalidCyrillic);
		Util.setInvalidCyrillicFlag(list, user);
		assertFalse(user.isHasInvalidCyrillic());
	}
	
}
