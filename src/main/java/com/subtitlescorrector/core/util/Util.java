package com.subtitlescorrector.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.CompositeEditOperation;
import com.subtitlescorrector.core.domain.EditOperation;
import com.subtitlescorrector.core.domain.SubtitleConversionFileDataResponse;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileDataWebDto;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileLineDataWebDto;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;

import jakarta.servlet.http.HttpServletRequest;


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
	
	public static AdditionalData extractOptions(HttpServletRequest request) {
		
		AdditionalData params = new AdditionalData();
		
		params.setStripBTags(Boolean.parseBoolean(request.getParameter("stripBTags")));
		params.setStripITags(Boolean.parseBoolean(request.getParameter("stripITags")));
		params.setStripFontTags(Boolean.parseBoolean(request.getParameter("stripFontTags")));
		params.setStripUTags(Boolean.parseBoolean(request.getParameter("stripUTags")));
		params.setKeepBOM(Boolean.parseBoolean(request.getParameter("keepBOM")));
		params.setConvertAeToTj(Boolean.parseBoolean(request.getParameter("aeToTj")));
		params.setConvertAEToTJ(Boolean.parseBoolean(request.getParameter("AEToTJ")));
		params.setConverteToch(Boolean.parseBoolean(request.getParameter("eToch")));
		params.setConvertEToCH(Boolean.parseBoolean(request.getParameter("EToCH")));
		params.setAiEnabled(Boolean.parseBoolean(request.getParameter("aiEnabled")));
		
		return params;
	}
	
	public static void populateBomData(BomData data, List<String> lines) {

		if (lines.get(0).startsWith("\uFEFF")) {
			data.setHasBom(true);
			data.setKeepBom(false);
		}else {
			data.setHasBom(false);
		}
	}
	
}
