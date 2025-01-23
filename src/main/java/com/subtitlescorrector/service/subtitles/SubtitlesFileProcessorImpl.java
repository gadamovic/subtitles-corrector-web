package com.subtitlescorrector.service.subtitles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.applicationproperties.ApplicationProperties;
import com.subtitlescorrector.controller.rest.FileUploadController;
import com.subtitlescorrector.domain.EditOperation;
import com.subtitlescorrector.domain.EditOperation.OperationType;
import com.subtitlescorrector.domain.S3BucketNames;
import com.subtitlescorrector.domain.SubtitleFileData;
import com.subtitlescorrector.domain.SubtitleUnitData;
import com.subtitlescorrector.domain.SubtitlesFileProcessorResponse;
import com.subtitlescorrector.domain.SubtitlesProcessingStatus;
import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;
import com.subtitlescorrector.service.processors.PreProcessor;
import com.subtitlescorrector.service.processors.PreProcessorsManager;
import com.subtitlescorrector.service.s3.S3Service;
import com.subtitlescorrector.service.subtitles.corrections.Corrector;
import com.subtitlescorrector.service.subtitles.corrections.CorrectorsManager;
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
	PreProcessorsManager preProcessorsManager;

	@Autowired
	S3Service s3Service;

	@Autowired
	Util util;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	SubtitleLinesToSubtitleUnitDataConverter converter;
	
	@Autowired
	EditDistanceService levenshteinDistance;

	@Override
	public SubtitleFileData process(File storedFile, String webSocketSessionId) {

		SubtitleFileData data = new SubtitleFileData();
		File correctedFile = new File(storedFile.getName());

		try {
			
			s3Service.uploadFileToS3IfProd("v1_" + webSocketSessionId, S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), storedFile);
			
			Charset detectedEncoding = FileUtil.detectEncodingOfFile(storedFile);
			List<String> lines = FileUtil.loadTextFile(storedFile);
			
			
			data.setFilename(correctedFile.getName());
			data.setLines(converter.convertToSubtitleUnits(lines));
			
			//preprocessors are not considered as subtitle corrections and won't be reported to the user as corrections
			//this are preparations of the content of the uploaded file
			for (PreProcessor preProcessor : preProcessorsManager.getPreProcessors()) {
				data = preProcessor.process(data);
			}
			
			for (Corrector corrector : correctorsManager.getCorrectors()) {
				data = corrector.correct(data, webSocketSessionId);
			}
			
			calculateEditOperationsAfterCorrections(data);

			if (detectedEncoding != StandardCharsets.UTF_8) {
				updateEncoding(storedFile, webSocketSessionId, detectedEncoding);
			}

			//TODO: upload file to s3 on multiple places (for ex. before edit, after corrections, before user save)
			FileUtil.writeLinesToFile(correctedFile, converter.convertToListOfStrings(data.getLines()), StandardCharsets.UTF_8);

			s3Service.uploadFileToS3IfProd("v2_" + webSocketSessionId, S3BucketNames.SUBTITLES_UPLOADED_FILES.getBucketName(), correctedFile);

		}catch (Exception e) {
			log.error("Error processing file!", e);
		} finally {
			deleteFiles(storedFile, correctedFile);
		}

		return data;
	}

	private void calculateEditOperationsAfterCorrections(SubtitleFileData data) {
		for(SubtitleUnitData subData : data.getLines()) {
			
			String original = subData.getTextBeforeCorrection();
			String corrected = subData.getText();
			
			List<EditOperation> operations = levenshteinDistance.getEditOperations(original, corrected);
			
			subData.setCompEditOperations(Util.convertToCompositeEditOperations(operations));
			
		}
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
