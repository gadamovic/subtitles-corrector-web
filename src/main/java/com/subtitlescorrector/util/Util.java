package com.subtitlescorrector.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

	public static String getCurrentTimestampAsString() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmssSSS"));
	}
	
}
