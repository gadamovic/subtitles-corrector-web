package com.subtitlescorrector.core.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.UserSubtitleConversionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleFileData;
import com.subtitlescorrector.core.service.conversion.AssSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.SrtSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;

public class JsonSerializationUtil {

	private static final Logger log = LoggerFactory.getLogger(JsonSerializationUtil.class);
	
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
	
}
