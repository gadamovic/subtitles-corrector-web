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
public class AiCorrector implements Corrector{

	private static final int AI_PROCESSING_CHUNK_SIZE = 100;

	Logger log = LoggerFactory.getLogger(AiCorrector.class);
	
	@Autowired
	Util util;
	
	@Autowired
	AiServiceImpl ai;
	
	@Autowired
	CustomWebSocketHandler webSocket;
	
	@Autowired
	ExecutorService executorService;
	
	String promptTemplate = null;
	
	@Override
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
		
		//after sending same event for the second time, FE will know ai processing is completed
		webSocket.sendMessage(createAiProcessingStartedEvent(webSocketSessionId));
		
		log.info("AI corrector finished after: " + (System.currentTimeMillis() - start));
		
		return data;
	}

	private void processOneChunk(ObjectMapper mapper, String webSocketSessionId,
			Map.Entry<Integer, List<SubtitleUnitData>> entry) {
		
		StringBuilder sb = new StringBuilder();
		for(SubtitleUnitData subUnitData : entry.getValue()) {
			sb.append(subUnitData.getNumber()).append("\n").append(subUnitData.getText()).append("\n\n");
		}
		
		
		String prompt = promptTemplate + "\n" + sb.toString();
		
		//TODO: Update logic to receive only updated lines from AI to save tokens
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

	@Override
	public void correct(SubtitleUnitData subUnit, AdditionalData params) {
		
		String line = subUnit.getText();
		
		String webSocketSessionId = params.getWebSocketSessionId();
		float processedPercentage = 0f;
		processedPercentage = ((float) params.getProcessedLines() / (float) params.getTotalNumberOfLines()) * 100;
				
		ObjectMapper mapper = new ObjectMapper();
		
		String tmp = "";
		String beforeCorrection = line;
		
		String prompt = promptTemplate + "\n" + line;
		
		String aiCorrectionResponseStr = ai.askOpenAi(prompt).getFirstChoiceMessage();
		
		CorrectionResponse aiCorrectionResponse = new CorrectionResponse();
		try {
			aiCorrectionResponse = mapper.readValue(aiCorrectionResponseStr, CorrectionResponse.class);
		} catch (Exception e) {
			log.error("Error deserializing json: " + aiCorrectionResponseStr, e);
			aiCorrectionResponse.setCorrection(line); //ignore correction
		}
		
		tmp = aiCorrectionResponse.getCorrection();
		beforeCorrection = util.checkForChanges(tmp, beforeCorrection, aiCorrectionResponse.getDescription(), processedPercentage, webSocketSessionId);
		
		if(line.equals(aiCorrectionResponse.getCorrection())) {
			log.info("No correction.");
		}
		
		subUnit.setText(beforeCorrection);
		
	}
	
}
