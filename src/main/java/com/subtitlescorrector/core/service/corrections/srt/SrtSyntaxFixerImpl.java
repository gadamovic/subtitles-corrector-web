package com.subtitlescorrector.core.service.corrections.srt;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;

@Service
public class SrtSyntaxFixerImpl implements SrtSyntaxFixer {
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	Logger log = LoggerFactory.getLogger(SrtSyntaxFixerImpl.class);
	
	private static final String SRT_TIMESTAMP_VALIDATOR = "^(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d,\\d{3} --> (?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d,\\d{3}$";
	private static final String SRT_TIMESTAMP_EXTRACTOR = "(\\d{1,2}:\\d{1,2}:\\d{1,2}(?:[.,]\\d{1,3})?)\\s*[-–—]*>*\\s*(\\d{1,2}:\\d{1,2}:\\d{1,2}(?:[.,]\\d{1,3})?)";
	
	public static final Pattern SRT_TIMESTAMP_EXTRACTOR_PATTERN = Pattern.compile(SRT_TIMESTAMP_EXTRACTOR);
	
	/**
	 * 
	 * @param line that is being validated
	 * @param allLines of the subtitle file
	 * @param currentIndex index of the line in the allLines list 
	 */
	public String validateAndFixTimestamp(String line, List<String> allLines, int currentIndex) {
		
		return line;
	}

	@Override
	public String validateAndFixTimestampLine(String line) {
		 if(!line.matches(SRT_TIMESTAMP_VALIDATOR) && line.matches(SRT_TIMESTAMP_EXTRACTOR)) {
			 
			 Matcher matcher = SRT_TIMESTAMP_EXTRACTOR_PATTERN.matcher(line);

			 if (matcher.find()) {

			     String from = matcher.group(1);
			     String to = matcher.group(2);

			     from = normalizeTimestamp(from);
			     to = normalizeTimestamp(to);

			     String fixed = from + " --> " + to;
			     
			     sendWebsocketMessage("Fixed timestamp from[" + line + "] to [" + fixed + "]");
			     
			     return fixed;
			 }
			 
		 }
		 return line;
	}
	
	private String normalizeTimestamp(String ts) {

		//this might be handled in parser already
	    ts = ts.replace('.', ',');

	    if (!ts.contains(",")) {
	        ts += ",000";
	    }

        String[] commaSplit = ts.split(",");

        String millis = commaSplit[1];

        if (millis.length() == 1) {
            millis += "00";
        } else if (millis.length() == 2) {
            millis += "0";
        } else if (millis.length() > 3) {
            millis = millis.substring(0, 3);
        }
        
        String[] colonSplit = ts.split(":");
        
        String seconds = colonSplit[colonSplit.length-1].split(",")[0];
        
        if(seconds.length() == 1) {
        	seconds += "0";
        }else if(seconds.length() > 2) {
        	seconds = seconds.substring(0, 2);
        }
        
        String[] hourMinuteSecond = commaSplit[0].split(":");
        
        ts = hourMinuteSecond[0] + ":" + hourMinuteSecond[1] + ":" + seconds + "," + millis;
	        
	    

	    // pad hour if needed
	    String[] parts = ts.split(":");

	    if (parts[0].length() == 1) {
	        parts[0] = "0" + parts[0];
	    }

	    return String.join(":", parts);
	}
	
	private void sendWebsocketMessage(String message) {
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		log.info(message);
		event.setCorrection(message);
		webSocketMessageSender.sendMessage(event);
	}
	
}
