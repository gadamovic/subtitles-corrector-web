package com.subtitlescorrector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;

import com.subtitlescorrector.adapters.out.S3ServiceAdapter;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitlesFileProcessor;
import com.subtitlescorrector.core.util.Constants;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.Util;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubtitleOverallProcessingIntegrationTest {

	@Autowired
	SrtSubtitlesFileProcessor processor;

	private AdditionalData data;

	private File testFile;

	@Test
	void test() {

		SrtSubtitleFileData fileData = processor.process(testFile, FileUtil.loadTextFile(testFile), data, null);
		
		assertEquals(fileData.getDetectedCharset(), StandardCharsets.UTF_8);
		assertEquals(fileData.getLines().size(), 8);
		
		assertEquals(fileData.getLines().get(0).getText(), "aaaaaaaaaaaa not permited tag<br>aaaaaaaaaaaa <font color=\"red\">permited tag</font>");
		assertEquals(fileData.getLines().get(0).getNumber(), 1);
		assertEquals(fileData.getLines().get(0).getFormat(), "srt");
		assertTrue(fileData.getLines().get(0).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(0).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(1).getText(), "multi<br>line");
		assertEquals(fileData.getLines().get(1).getNumber(), 2);
		assertEquals(fileData.getLines().get(1).getFormat(), "srt");
		assertTrue(fileData.getLines().get(1).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(1).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(2).getText(), "Ћирилица");
		assertEquals(fileData.getLines().get(2).getNumber(), 3);
		assertEquals(fileData.getLines().get(2).getFormat(), "srt");
		assertTrue(fileData.getLines().get(2).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(2).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(3).getText(), "Ćirilica");
		assertEquals(fileData.getLines().get(3).getNumber(), 4);
		assertEquals(fileData.getLines().get(3).getFormat(), "srt");
		assertTrue(fileData.getLines().get(3).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(3).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(4).getText(), "asdasdasd");
		assertEquals(fileData.getLines().get(4).getNumber(), 5);
		assertEquals(fileData.getLines().get(4).getFormat(), "srt");
		assertTrue(fileData.getLines().get(4).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(4).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(5).getText(), "Nemilosrdna je prema svemu<br>lažnom. Ali zato je i predivna.");
		assertEquals(fileData.getLines().get(5).getNumber(), 6);
		assertEquals(fileData.getLines().get(5).getFormat(), "srt");
		assertTrue(fileData.getLines().get(5).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(5).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(6).getText(), "test");
		assertEquals(fileData.getLines().get(6).getNumber(), 7);
		assertEquals(fileData.getLines().get(6).getFormat(), "srt");
		assertTrue(fileData.getLines().get(6).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(6).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(7).getText(), "ćĆčČ");
		assertEquals(fileData.getLines().get(7).getNumber(), 8);
		assertEquals(fileData.getLines().get(7).getFormat(), "srt");
		assertTrue(fileData.getLines().get(7).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(7).getTimestampTo() != null);


	}

	@BeforeAll
	void init() {

		data = new AdditionalData();
		data.setConvertAeToTj(true);
		data.setConvertAEToTJ(true);
		data.setConverteToch(true);
		data.setConvertEToCH(true);

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle-test.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
