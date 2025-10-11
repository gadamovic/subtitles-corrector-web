package com.subtitlescorrector.core.util;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.CompositeEditOperation;
import com.subtitlescorrector.core.domain.EditOperation;
import com.subtitlescorrector.core.domain.SecondMillisecondDelimiterRegex;
import com.subtitlescorrector.core.domain.SubtitleConversionFileDataResponse;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.TimeUnit;
import com.subtitlescorrector.core.domain.UserSubtitleConversionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.service.conversion.AssSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.SrtSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileDataWebDto;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileLineDataWebDto;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleFileData;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;


@Component
public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;

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

	public static String srtSubtitleFileDataToJson(SrtSubtitleFileData data) {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting SubtitleFileData to json", e);
			return null;
		}
	}

	public static SrtSubtitleFileData jsonToSrtSubtitleFileData(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, SrtSubtitleFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing saying: " + json, e);
			return null;
		}
	}
	
	public static String vttSubtitleFileDataToJson(VttSubtitleFileData data) {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting SubtitleFileData to json", e);
			return null;
		}
	}

	public static VttSubtitleFileData jsonToVttSubtitleFileData(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, VttSubtitleFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing saying: " + json, e);
			return null;
		}
	}
	
	public static String assSubtitleFileDataToJson(AssSubtitleFileData data) {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting SubtitleFileData to json", e);
			return null;
		}
	}

	public static AssSubtitleFileData jsonToAssSubtitleFileData(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, AssSubtitleFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing saying: " + json, e);
			return null;
		}
	}
	
	public static String vttSubtitleConversionFileDataToJson(VttSubtitleConversionFileData data) { 
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting subtitleConversionFileData to json", e);
			return null;
		}
	}

	public static VttSubtitleConversionFileData jsonToVttSubtitleConversionFileData(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, VttSubtitleConversionFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing SubtitleConversionFileData: " + json, e);
			return null;
		}
	}
	
	public static String assSubtitleConversionFileDataToJson(AssSubtitleConversionFileData data) { 
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting AssSubtitleConversionFileData to json", e);
			return null;
		}
	}

	public static AssSubtitleConversionFileData jsonToAssSubtitleConversionFileData(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, AssSubtitleConversionFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing AssSubtitleConversionFileData: " + json, e);
			return null;
		}
	}
	
	public static String srtSubtitleConversionFileDataToJson(SrtSubtitleConversionFileData data) { 
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting SrtSubtitleConversionFileData to json", e);
			return null;
		}
	}

	public static SrtSubtitleConversionFileData jsonToSrtSubtitleConversionFileData(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, SrtSubtitleConversionFileData.class);
		} catch (IOException e) {
			log.error("Error deserializing SrtSubtitleConversionFileData: " + json, e);
			return null;
		}
	}

	public static UserSubtitleCorrectionCurrentVersionMetadata jsonToSubtitleCurrentVersionMetadata(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, UserSubtitleCorrectionCurrentVersionMetadata.class);
		} catch (IOException e) {
			log.error("Error deserializing UserSubtitleCurrentVersionMetadata: " + json, e);
			return null;
		}
	}
	
	public static String userSubtitleCurrentVersionMetadataToJson(UserSubtitleCorrectionCurrentVersionMetadata data) { 
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting SubtitleCurrentVersionMetadataToJson to json", e);
			return null;
		}
	}
	
	public static String userSubtitleConversionCurrentVersionMetadataToJson(UserSubtitleConversionCurrentVersionMetadata data) { 
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error converting SubtitleCurrentConversionVersionMetadataToJson to json", e);
			return null;
		}
	}
	
	public static UserSubtitleConversionCurrentVersionMetadata jsonToUserSubtitleConversionCurrentVersionMetadata(String json) {
		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(json, UserSubtitleConversionCurrentVersionMetadata.class);
		} catch (IOException e) {
			log.error("Error deserializing UserSubtitleConversionCurrentVersionMetadata: " + json, e);
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
	
	//TODO: Same method defined in AbstractCorrector.java
	public String checkForChanges(String afterCorrection, String beforeCorrection, String correctionDescription,
			float processedPercentage) {
		if (!afterCorrection.equals(beforeCorrection)) {

			log.info("Before correction: " + beforeCorrection);
			log.info("After correction : " + afterCorrection);

			SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
			event.setCorrection(correctionDescription);
			event.setEventTimestamp(Instant.now());

			event.setProcessedPercentage(String.valueOf(processedPercentage));

			if (properties.getSubtitlesRealTimeUpdatesEnabled()) {
				webSocketMessageSender.sendMessage(event);
			}

		}

		return afterCorrection;
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

			if(line.equals("[Script Info]")) {
				return SubtitleFormat.ASS;
			}
			
		}

		return SubtitleFormat.SRT;

	}
	
	public SubtitleTimestamp parseSubtitleTimestampString(String timestampString, SecondMillisecondDelimiterRegex delimiter) {
		return parseSubtitleTimestampString(timestampString, delimiter, 1);
	}
	
	/**
	 * 
	 * @param timestampString
	 * @param secondMillisecondDelimiterRegex
	 * @param millisecondFactor to convert some other time unit to milliseconds. For example centisecond -> millisecond (factor = 10)
	 * @return
	 */
	public SubtitleTimestamp parseSubtitleTimestampString(String timestampString, SecondMillisecondDelimiterRegex delimiter, 
			int millisecondFactor) {
		
		SubtitleTimestamp ts = new SubtitleTimestamp();
		String[] fromSplit = timestampString.split(":");
		ts.setHour(Short.parseShort(fromSplit[0]));
		ts.setMinute(Short.parseShort(fromSplit[1]));
		
		List<SecondMillisecondDelimiterRegex> allDelimiters = loadAllDelimiters(delimiter);
		
		boolean parseExceptionOccured = false;
		
		for(SecondMillisecondDelimiterRegex current : allDelimiters) {
		
			String secondMillisecondSplit[] = fromSplit[2].split(current.getRegex());
			
			try {
				ts.setSecond(Short.parseShort(secondMillisecondSplit[0]));
				ts.setMillisecond((short) (Short.parseShort(secondMillisecondSplit[1]) * millisecondFactor));
				
				if(parseExceptionOccured) {
					notifyUserParseExceptionWasFixed(secondMillisecondSplit, current);
				}
				
				break;
			}catch(NumberFormatException npe) {
				parseExceptionOccured = true;
				MDC.put("timestamp_value", Arrays.toString(secondMillisecondSplit));
				String message = "Timestamp parsing error for timestamp: " + Arrays.toString(secondMillisecondSplit);
				log.error(message);
				MDC.remove("timestamp_value");
			}
			
		}
		return ts;
	}
	
	private void notifyUserParseExceptionWasFixed(String[] secondMillisecondSplit,
			SecondMillisecondDelimiterRegex current) {
		
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setCorrection("Timestamp parsing error was fixed using: '" + current.getFormattedDelimiter() + "' delimiter.");
		event.setEventTimestamp(Instant.now());

		event.setProcessedPercentage("0");

		if (properties.getSubtitlesRealTimeUpdatesEnabled()) {
			webSocketMessageSender.sendMessage(event);
		}
		
	}

	/**
	 * Creates a list of all existing delimiters with the one passed as a parameter being the first
	 * and others are used as backup if the first one fails. Meant to bypass subtitle file's formatting
	 * errors when for example in SRT file, instead of comma, dot is used to separate seconds from milliseconds 
	 * in a timestamp
	 * @param delimiter
	 * @return
	 */
	private static List<SecondMillisecondDelimiterRegex> loadAllDelimiters(SecondMillisecondDelimiterRegex delimiter) {
		
		List<SecondMillisecondDelimiterRegex> list = new ArrayList<>();
		list.add(delimiter);
		
		for(SecondMillisecondDelimiterRegex current : SecondMillisecondDelimiterRegex.values()) {
			if(!list.contains(current)) {
				list.add(current);
			}
		}
		
		return list;
	}

	public static String formatTimestamp(SubtitleTimestamp timestamp, String secondMillisecondDelimiter) {
		return formatTimestamp(timestamp, secondMillisecondDelimiter, TimeUnit.MILLISECOND);
	}
	
	/**
	 * 
	 * @param timestamp
	 * @param secondMillisecondDelimiter
	 * @param smallestTimeunitType type of the smallest and last unit of time in the timestamp notation
	 * @return
	 */
	public static String formatTimestamp(SubtitleTimestamp timestamp, String secondMillisecondDelimiter, TimeUnit smallestTimeunitType) {
		String hour = timestamp.getHour() < 10 ? "0" + timestamp.getHour() : String.valueOf(timestamp.getHour());
		String minute = timestamp.getMinute() < 10 ? "0" + timestamp.getMinute() : String.valueOf(timestamp.getMinute());
		String second = timestamp.getSecond() < 10 ? "0" + timestamp.getSecond() : String.valueOf(timestamp.getSecond());
		String smallestTimeunit = null;
		
		if(smallestTimeunitType == TimeUnit.MILLISECOND) {
			if(timestamp.getMillisecond() < 10) {
				smallestTimeunit = "00" + String.valueOf(timestamp.getMillisecond());
			}else if (timestamp.getMillisecond() < 100) {
				smallestTimeunit = "0" + String.valueOf(timestamp.getMillisecond());
			}else {
				smallestTimeunit = String.valueOf(timestamp.getMillisecond());
			}
		}else if(smallestTimeunitType == TimeUnit.CENTISECOND) {
			
			short smallestTimeunitValue = (short) (timestamp.getMillisecond() / 10);
			
			if(smallestTimeunitValue < 10) {
				smallestTimeunit = "0" + String.valueOf(smallestTimeunitValue);
			}else {
				smallestTimeunit = String.valueOf(smallestTimeunitValue);
			}
			
		}
		
		String formattedTimestamp = hour + ":" + minute + ":" + second + secondMillisecondDelimiter + smallestTimeunit;
		return formattedTimestamp;
	}
	

	public static SubtitleConversionFileDataResponse subtitleConversionFileDataToResponseObject(VttSubtitleConversionFileData conversionFileData) {
		
		SubtitleConversionFileDataResponse response = new SubtitleConversionFileDataResponse();
		response.setDetectedEncoding(conversionFileData.getDetectedEncoding());
		response.setDetectedSourceFormat(conversionFileData.getSourceFormat());
		response.setFilename(conversionFileData.getFilename());
		response.setNumberOfLines(conversionFileData.getLines().size());
		
		return response;
		
	}

	public static String subtitleConversionFileDataResponseToJson(SubtitleConversionFileDataResponse conversionFileDataToResponseObject) {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(conversionFileDataToResponseObject);
		} catch (JsonProcessingException e) {
			log.error("Error converting SubtitleConversionFileDataResponse to json", e);
			return null;
		}
	}

	/**
	 * For html line breaks are represented as br tags, so put them back to \n
	 */
	public static void convertBrTagsToNewLineCharacters(SrtSubtitleFileData subtitleData) {
		for(SrtSubtitleUnitData data : subtitleData.getLines()) {
			data.setText(data.getText().replace("<br>", "\n"));
			data.setTextBeforeCorrection(data.getTextBeforeCorrection().replace("<br>", "\n"));
		}
	}
	
	/**
	 * For html line breaks are represented as br tags, so put them back to \n
	 */
	public static void convertBrTagsToNewLineCharacters(SubtitleCorrectionFileDataWebDto subtitleData) {
		for(SubtitleCorrectionFileLineDataWebDto data : subtitleData.getLines()) {
			data.setText(data.getText().replace("<br>", "\n"));
			data.setTextBeforeCorrection(data.getTextBeforeCorrection().replace("<br>", "\n"));
		}
	}
	
	public static List<String> addBom(List<String> lines) {
		
		if(lines.size() > 0) {
			String firstLine = lines.get(0);
			if(!firstLine.startsWith("\uFEFF")) {
				firstLine = "\uFEFF" + firstLine;
				lines.set(0, firstLine);
			}
		}
		return lines;
	}
	
	public static void removeBomIfExists(List<String> lines) {
		//there can be multiple BOMs
		int b=0;
		while (lines.get(0).startsWith("\uFEFF")) {
			b++;
			// Remove BOM
			String line = lines.get(0).substring(1);
			lines.remove(0);
			lines.add(0, line);
		}
		
		if(b > 1) {
			log.info("There was more then one ({}) BOM in the file", b);
		}
	}
	
}
