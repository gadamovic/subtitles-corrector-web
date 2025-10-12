package com.subtitlescorrector.core.service.corrections;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;
import com.subtitlescorrector.core.domain.ai.CorrectionResponse;
import com.subtitlescorrector.core.domain.ai.CorrectionsWrapper;
import com.subtitlescorrector.core.domain.ai.LineForAiCorrection;
import com.subtitlescorrector.core.port.AiServicePort;
import com.subtitlescorrector.core.service.websocket.WebSocketMessageSender;
import com.subtitlescorrector.core.util.Util;

import jakarta.annotation.PostConstruct;

@Service
/**
 * Custom corrector that works with the entire subtitles file instead of one subtitle line as other correctors
 * that are subclasses of AbstractCorrector.java
 * @author Gavrilo Adamovic
 *
 */
public class AiCustomCorrectorImpl implements AiCustomCorrector{

	private static final String CORRECTION_AI_PROMPT_TXT_FILE_PATH = "openAiCorrectionPrompt.txt";

	private static final int AI_PROCESSING_CHUNK_SIZE = 70;

	Logger log = LoggerFactory.getLogger(AiCustomCorrectorImpl.class);
	
	@Autowired
	Util util;
	
	@Autowired
	AiServicePort ai;
	
	@Autowired
	WebSocketMessageSender webSocketMessageSender;
	
	@Autowired
	ExecutorService executorService;
	
	String promptTemplate = null;
	
	@Override
	public List<LineForAiCorrection> correct(List<LineForAiCorrection> lines, CorrectorParameters params) {
		
		log.info("AI corrector started...");
		Long start = System.currentTimeMillis();
		
		ObjectMapper mapper = new ObjectMapper();

		webSocketMessageSender.sendMessage(createAiProcessingStartedEvent());
		Map<Integer, List<LineForAiCorrection>> partitioned = partitionSubUnits(lines, AI_PROCESSING_CHUNK_SIZE);
		
		List<Future<?>> futures = new ArrayList<>();
		
		// Get and then pass request attributes from current thread to the new one
		// so session scoped data could be obtained
		RequestAttributes context = RequestContextHolder.getRequestAttributes();
		
		for(Map.Entry<Integer, List<LineForAiCorrection>> entry : partitioned.entrySet()) {
			
			Future<?> f = executorService.submit(() -> {
				RequestContextHolder.setRequestAttributes(context);
				processOneChunk(mapper, entry);
			});
			futures.add(f);
				
		}
		
		futures.forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {}
		});
		
		webSocketMessageSender.sendMessage(createAiProcessingEndedEvent());
		
		logAICorrectorFinished(start);
		
		return lines;
	}

	/**
	 * after sending same event for the second time, FE will know ai processing is completed
	 * @return
	 */
	private SubtitleCorrectionEvent createAiProcessingEndedEvent() {
		return createAiProcessingStartedEvent();
	}

	private void logAICorrectorFinished(Long start) {
		MDC.put("execution_time", Long.toString(System.currentTimeMillis() - start));
		log.info("AI corrector finished...");
		MDC.remove("execution_time");
	}

	private void processOneChunk(ObjectMapper mapper, Map.Entry<Integer, List<LineForAiCorrection>> entry) {
		
		StringBuilder sb = new StringBuilder();
		for(LineForAiCorrection aiLine : entry.getValue()) {
			sb.append(aiLine.getNumber()).append("\n").append(aiLine.getText()).append("\n\n");
		}
		
		String aiCorrectionResponseStr = ai.askOpenAi(promptTemplate, sb.toString()).getFirstChoiceMessage();
		
		CorrectionsWrapper aiCorrectionsWrapper = new CorrectionsWrapper();
		try {
			aiCorrectionsWrapper = mapper.readValue(aiCorrectionResponseStr, CorrectionsWrapper.class);
		} catch (Exception e) {
			log.error("Error deserializing json: " + aiCorrectionResponseStr, e);
			return;
		}
		
		Map<String, List<CorrectionResponse>> responsesByLineNumber = aiCorrectionsWrapper.getCorrections().stream()
				.collect(Collectors.groupingBy(CorrectionResponse::getNumber));
		
		for(LineForAiCorrection aiLine : entry.getValue()) {
			
			String line = aiLine.getText();
			String number = aiLine.getNumber().toString();
			
			CorrectionResponse resp = (responsesByLineNumber.containsKey(number) && !responsesByLineNumber.get(number).isEmpty()) ?
					responsesByLineNumber.get(number).get(0) : null;
			
			if(resp != null && !resp.getDescription().equals("-1")) {
				line = util.checkForChanges(resp.getCorrection(), line, resp.getDescription(), 100);
			}
			
			aiLine.setText(line);
		}
	}
	
	private Map<Integer, List<LineForAiCorrection>> partitionSubUnits(List<LineForAiCorrection> data, int chunkSize) {
		
		AtomicInteger i = new AtomicInteger(0);
		return data.stream().collect(Collectors.groupingBy((d) -> i.getAndIncrement() / chunkSize,
		        LinkedHashMap::new, // preserve order of keys
		        Collectors.toList()));
		
	}

	private SubtitleCorrectionEvent createAiProcessingStartedEvent() {
		
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setCorrection("ai-processing-started");
		event.setEventTimestamp(Instant.now());
		
		return event;
	}

	@PostConstruct
	private void initPrompt() {
		try (InputStream is = new ClassPathResource(CORRECTION_AI_PROMPT_TXT_FILE_PATH).getInputStream();
				Scanner scanner = new Scanner(is);){
			
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNextLine()) {
				sb.append(scanner.nextLine()).append("\n");
			}
			
			promptTemplate = sb.toString();
			
		} catch (IOException e) {
			log.error("Error reading openAiCorrectionPrompt.txt file!", e);
		}
	}
	
}
