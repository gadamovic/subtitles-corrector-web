package com.subtitlescorrector.service.subtitles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.controller.rest.FileUploadController;
import com.subtitlescorrector.domain.SubtitlesFileProcessorResponse;
import com.subtitlescorrector.domain.SubtitlesProcessingStatus;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.CorrectorsManager;
import com.subtitlescorrector.service.s3.S3Service;
import com.subtitlescorrector.service.subtitles.corrections.Corrector;
import com.subtitlescorrector.util.Constants;
import com.subtitlescorrector.util.FileUtil;
import com.subtitlescorrector.util.Util;

@Component
public class SubtitlesFileProcessorImpl implements SubtitlesFileProcessor {

	Logger log = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	ApplicationProperties properties;

	@Autowired
	KafkaTemplate<Void, SubtitleCorrectionEvent> kafkaTemplate;

	@Autowired
	CorrectorsManager correctorsManager;

	@Autowired
	S3Service s3Service;

	@Autowired
	Util util;
	
	@Autowired
	MailSender mailSender;

	@Override
	public SubtitlesFileProcessorResponse process(File storedFile, String webSocketSessionId) {

		File correctedFile = new File(storedFile.getName());
		SubtitlesFileProcessorResponse response = new SubtitlesFileProcessorResponse();

		try {

			Charset detectedEncoding = FileUtil.detectEncodingOfFile(storedFile);
			List<String> lines = FileUtil.loadTextFile(storedFile);

			for (Corrector corrector : correctorsManager.getCorrectors()) {
				lines = corrector.correct(lines, webSocketSessionId);
			}

			if (detectedEncoding != StandardCharsets.UTF_8) {
				updateEncoding(storedFile, webSocketSessionId, detectedEncoding);
			}

			FileUtil.writeLinesToFile(correctedFile, lines, StandardCharsets.UTF_8);

			response = s3Service.uploadAndGetDownloadUrl(correctedFile);
			response.setLines(lines);
			
		}catch (Exception e) {
			log.error("Error processing file!", e);
		} finally {
			deleteFiles(storedFile, correctedFile);
		}

		return response;
	}

	private void deleteFiles(File storedFile, File correctedFile) {
		try {
			Files.delete(storedFile.toPath());
			Files.delete(correctedFile.toPath());
		} catch (IOException e) {
			log.error("Error deleting files!", e);
		}
	}

	private void updateEncoding(File storedFile, String webSocketSessionId, Charset detectedEncoding) {
		log.info("Updated encoding of: {} to UTF-8!", storedFile.getName());

		SubtitleCorrectionEvent encodingUpdate = new SubtitleCorrectionEvent();
		encodingUpdate.setCorrection("Encoding updated: " + detectedEncoding + " -> UTF-8");
		encodingUpdate.setWebSocketSessionId(webSocketSessionId);
		kafkaTemplate.send(Constants.SUBTITLES_CORRECTIONS_TOPIC_NAME, encodingUpdate);
	}

}
