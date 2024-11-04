package com.subtitlescorrector.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);
	
	public static String getCurrentTimestampAsString() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmssSSS"));
	}

	public static Map<String, String> loadPropertiesFileIntoMap(File props) {

		Map<String, String> propsMap = new HashMap<String, String>();

		Scanner scanner = null;

		try {
			scanner = new Scanner(props);
		} catch (FileNotFoundException e) {
			log.error("Error loading properties into map!", e);
		}

		if (scanner != null) {

			while (scanner.hasNextLine()) {

				String line = scanner.nextLine();
				String keyVal[] = line.split("=");
				if (keyVal.length == 2 && isNotBlank(keyVal[0]) && !keyVal[0].startsWith("#")) {
					propsMap.put(keyVal[0], keyVal[1]);
				} else if (isNotBlank(keyVal[0]) && !keyVal[0].startsWith("#")) {
					log.error("Error parsing key-value pair: " + line + ". It will be skipped.");
				}

			}

		}

		scanner.close();

		return propsMap;
	}

	public static boolean isBlank(CharSequence cs) {

		int strLen;
		if (cs != null && (strLen = cs.length()) != 0) {
			for (int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(cs.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

}
