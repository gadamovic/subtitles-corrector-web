package com.subtitlescorrector.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.domain.CompositeEditOperation;
import com.subtitlescorrector.domain.EditOperation;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleFormat;
import com.subtitlescorrector.domain.SubtitleTimestamp;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.CustomWebSocketHandler;

@Component
public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	@Autowired
	ApplicationProperties properties;
	
//	@Autowired
//	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;
	
	@Autowired
	CustomWebSocketHandler webSocketHandler;

	public static String getCurrentTimestampAsString() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmssSSS"));
	}

	public static Map<String, String> loadPropertiesFileIntoMap(InputStream is) {

		File file = inputStreamToFile(is);

		Map<String, String> propsMap = new HashMap<String, String>();

		Scanner scanner = null;

		try {
			scanner = new Scanner(file);
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

		file.delete();
		return propsMap;
	}

	public static File inputStreamToFile(InputStream inputStream) {

		File file = new File("temp" + getCurrentTimestampAsString() + ".properties");
		try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {

			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

		} catch (Exception e) {
			log.error("Error writing input stream to file!");
		}
		return file;
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

	// NON-STATIC method!
	public String makeFilenameForDownloadFromS3Key(String s3Key) {
		return properties.getSubtitleCorrectionProcessedFileNamePrefix() + s3Key.substring(s3Key.indexOf('_') + 1);
	}

	/**
	 * Custom json serialization of SubtitlesCorrectionEvent because it is an
	 * avro-generated class and contains many fields
	 * 
	 * @param event
	 * @return
	 */
	public static String subtitleCorrectionEventToJson(SubtitleCorrectionEvent event) {

		JsonObject json = new JsonObject();
		if (event.getCorrection() != null) {
			json.addProperty("correctionDescription", event.getCorrection().toString());
		}

		if (event.getProcessedPercentage() != null) {
			json.addProperty("processedPercentage", event.getProcessedPercentage().toString());
		}
		return json.toString();

	}

	public static String subtitleUnitDataListToJson(SubtitleFileData data) {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting saying subtitleUnitData list to json", e);
			return null;
		}
	}

	public static SubtitleFileData jsonToSubtitleUnitDataList(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, SubtitleFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing saying: " + json, e);
			return null;
		}
	}

	public static String generateS3Key(String postfix) {

		String s3KeyUUIDPrefix = UUID.randomUUID().toString();
		String key = s3KeyUUIDPrefix + postfix;
		return key;

	}

	public static List<List<EditOperation>> groupConsecutiveEditOperations(List<EditOperation> operations) {

		List<List<EditOperation>> result = new ArrayList<>();

		List<EditOperation> currentGroup = new ArrayList<>();

		for (EditOperation operation : operations) {

			if (currentGroup.size() == 0 || currentGroup.get(0).getType() == operation.getType()) {
				currentGroup.add(operation);
			} else {
				result.add(currentGroup);
				currentGroup = new ArrayList<>();
				currentGroup.add(operation);
			}

		}

		if (currentGroup.size() > 0) {
			result.add(currentGroup);
		}

		return result;
	}

	public static List<CompositeEditOperation> convertToCompositeEditOperations(List<EditOperation> operations) {

		List<List<EditOperation>> grouped = groupConsecutiveEditOperations(operations);

		List<CompositeEditOperation> composite = new ArrayList<>();

		for (List<EditOperation> group : grouped) {

			CompositeEditOperation comp = new CompositeEditOperation();
			comp.setType(group.get(0).getType());
			
			for (EditOperation operation : group) {

				if (operation.getChar1() != null) {
					comp.appendToString1(operation.getChar1());
				}

				if (operation.getChar2() != null) {
					comp.appendToString2(operation.getChar2());
				}

			}

			composite.add(comp);

		}
		return composite;
	}
	
   public String checkForChanges(String afterCorrection, String beforeCorrection, String correctionDescription, float processedPercentage, String webSocketSessionId) {
               if(!afterCorrection.equals(beforeCorrection)) {

                       log.info("Before correction: " + beforeCorrection);
                       log.info("After correction : " + afterCorrection);

                       SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
                       event.setCorrection(correctionDescription);
                       event.setEventTimestamp(Instant.now());

                       event.setProcessedPercentage(String.valueOf(processedPercentage));
                       event.setWebSocketSessionId(webSocketSessionId);

                       if(properties.getSubtitlesRealTimeUpdatesEnabled()) {
                    	   webSocketHandler.sendMessage(event);
                       }

               }

               return afterCorrection;
       }
	
	public void sendWebSocketCorrectionMessageToKafka(String webSocketSessionId, String message) {
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setCorrection(message);
		event.setEventTimestamp(Instant.now());

		event.setWebSocketSessionId(webSocketSessionId);
					
		if(properties.getSubtitlesRealTimeUpdatesEnabled()) {
			//kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, event);
			webSocketHandler.sendMessage(event);
		}
	}

	public static SubtitleFormat detectSubtitleFormat(List<String> lines) {

		for (String line : lines) {
			if (line.startsWith("\uFEFF")) {
				// remove BOM
				line = line.substring(1);
			}

			if (line.equals("WEBVTT")) {
				return SubtitleFormat.VTT;
			}

		}

		return SubtitleFormat.SRT;

	}
	
	public static SubtitleTimestamp parseSubtitleTimestampString(String timestampString, String secondMillisecondDelimiterRegex) {
		
		SubtitleTimestamp ts = new SubtitleTimestamp();
		String[] fromSplit = timestampString.split(":");
		ts.setHour(Short.parseShort(fromSplit[0]));
		ts.setMinute(Short.parseShort(fromSplit[1]));
		
		String secondMillisecondSplit[] = fromSplit[2].split(secondMillisecondDelimiterRegex);
		ts.setSecond(Short.parseShort(secondMillisecondSplit[0]));
		ts.setMillisecond(Short.parseShort(secondMillisecondSplit[1]));
		
		return ts;
	}
	
	public static String formatTimestamp(SubtitleTimestamp timestamp, String secondMillisecondDelimiter) {
		String hour = timestamp.getHour() < 10 ? "0" + timestamp.getHour() : String.valueOf(timestamp.getHour());
		String minute = timestamp.getMinute() < 10 ? "0" + timestamp.getMinute() : String.valueOf(timestamp.getMinute());
		String second = timestamp.getSecond() < 10 ? "0" + timestamp.getSecond() : String.valueOf(timestamp.getSecond());
		String millisecond = null;
		
		if(timestamp.getMillisecond() < 10) {
			millisecond = "00" + String.valueOf(timestamp.getMillisecond());
		}else if (timestamp.getMillisecond() < 100) {
			millisecond = "0" + String.valueOf(timestamp.getMillisecond());
		}else {
			millisecond = String.valueOf(timestamp.getMillisecond());
		}
		
		String formattedTimestamp = hour + ":" + minute + ":" + second + secondMillisecondDelimiter + millisecond;
		return formattedTimestamp;
	}
	

}
