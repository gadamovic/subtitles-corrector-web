package com.subtitlescorrector.service.subtitles.corrections;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.domain.openai.CorrectionResponse;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.CustomWebSocketHandler;
import com.subtitlescorrector.service.ai.AiServiceImpl;
import com.subtitlescorrector.util.Util;

import jakarta.annotation.PostConstruct;

@Service
/**
 * Custom corrector that works with the entire subtitles file instead of one subtitle line as other correctors
 * that are subclasses of AbstractCorrector.java
 * @author Gavrilo Adamovic
 *
 */
public class AiCustomCorrector{

	private static final int AI_PROCESSING_CHUNK_SIZE = 100;

	Logger log = LoggerFactory.getLogger(AiCustomCorrector.class);
	
	@Autowired
	Util util;
	
	@Autowired
	AiServiceImpl ai;
	
	@Autowired
	CustomWebSocketHandler webSocket;
	
	@Autowired
	ExecutorService executorService;
	
	String promptTemplate = null;
	
	public SubtitleFileData correct(SubtitleFileData data, AdditionalData params) {
		
		log.info("AI corrector started...");
		Long start = System.currentTimeMillis();
		
		ObjectMapper mapper = new ObjectMapper();

		String webSocketSessionId = params.getWebSocketSessionId();
		webSocket.sendMessage(createAiProcessingStartedEvent(webSocketSessionId));
		Map<Integer, List<SubtitleUnitData>> partitioned = partitionSubUnits(data.getLines(), AI_PROCESSING_CHUNK_SIZE);
		
		List<Future<?>> futures = new ArrayList<>();
		
		for(Map.Entry<Integer, List<SubtitleUnitData>> entry : partitioned.entrySet()) {
			
			Future<?> f = executorService.submit(() -> processOneChunk(mapper, webSocketSessionId, entry));
			futures.add(f);
				
		}
		
		futures.forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {}
		});
		
		webSocket.sendMessage(createAiProcessingEndedEvent(webSocketSessionId));
		
		logAICorrectorFinished(data, start);
		
		return data;
	}

	/**
	 * after sending same event for the second time, FE will know ai processing is completed
	 * @param webSocketSessionId
	 * @return
	 */
	private SubtitleCorrectionEvent createAiProcessingEndedEvent(String webSocketSessionId) {
		return createAiProcessingStartedEvent(webSocketSessionId);
	}

	private void logAICorrectorFinished(SubtitleFileData data, Long start) {
		MDC.put("execution_time", Long.toString(System.currentTimeMillis() - start));
		MDC.put("subtitle_name", data.getFilename());
		log.info("AI corrector finished...");
		MDC.remove("execution_time");
		MDC.remove("subtitle_name");
	}

	private void processOneChunk(ObjectMapper mapper, String webSocketSessionId,
			Map.Entry<Integer, List<SubtitleUnitData>> entry) {
		
		StringBuilder sb = new StringBuilder();
		for(SubtitleUnitData subUnitData : entry.getValue()) {
			sb.append(subUnitData.getNumber()).append("\n").append(subUnitData.getText()).append("\n\n");
		}
		
		
		String prompt = promptTemplate + "\n" + sb.toString();
		
		String aiCorrectionResponseStr = ai.askOpenAi(prompt).getFirstChoiceMessage();
		
		List<CorrectionResponse> aiCorrectionResponses = new ArrayList<>();
		try {
			aiCorrectionResponses = mapper.readValue(aiCorrectionResponseStr, new TypeReference<List<CorrectionResponse>>() {});
		} catch (Exception e) {
			log.error("Error deserializing json: " + aiCorrectionResponseStr, e);
			return;
		}
		
		Map<String, List<CorrectionResponse>> responsesByLineNumber = aiCorrectionResponses.stream()
				.collect(Collectors.groupingBy(CorrectionResponse::getNumber));
		
		for(SubtitleUnitData subUnitData : entry.getValue()) {
			
			String line = subUnitData.getText();
			String number = subUnitData.getNumber().toString();
			
			CorrectionResponse resp = (responsesByLineNumber.containsKey(number) && !responsesByLineNumber.get(number).isEmpty()) ?
					responsesByLineNumber.get(number).get(0) : null;
			
			if(resp != null && !resp.getDescription().equals("-1")) {
				line = util.checkForChanges(resp.getCorrection(), line, resp.getDescription(), 100, webSocketSessionId);
			}
			
			subUnitData.setText(line);
		}
	}
	
	private Map<Integer, List<SubtitleUnitData>> partitionSubUnits(List<SubtitleUnitData> data, int chunkSize) {
		
		AtomicInteger i = new AtomicInteger(0);
		return data.stream().collect(Collectors.groupingBy((d) -> i.getAndIncrement() / chunkSize,
		        LinkedHashMap::new, // preserve order of keys
		        Collectors.toList()));
		
	}

	private SubtitleCorrectionEvent createAiProcessingStartedEvent(String webSocketSessionId) {
		
		SubtitleCorrectionEvent event = new SubtitleCorrectionEvent();
		event.setCorrection("ai-processing-started");
		event.setEventTimestamp(Instant.now());

		event.setWebSocketSessionId(webSocketSessionId);
		
		return event;
	}

	@PostConstruct
	private void initPrompt() {
		try (InputStream is = new ClassPathResource("openAiCorrectionPrompt.txt").getInputStream();
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
