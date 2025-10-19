package com.subtitlescorrector.core.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.adapters.out.configuration.ApplicationProperties;
import com.subtitlescorrector.core.domain.SecondMillisecondDelimiterRegex;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.TimeUnit;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;

@Component
public class SubtitleTimestampUtils {

	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	private static final Logger log = LoggerFactory.getLogger(SubtitleTimestampUtils.class);

	
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
			}catch(NumberFormatException nfe) {
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
	
}
