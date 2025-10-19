package com.subtitlescorrector.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;

import com.subtitlescorrector.adapters.out.RedisServiceAdapter;
import com.subtitlescorrector.adapters.out.WebSocketOutboundAdapter;
import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.SubtitleFileProviderForUserServiceImpl;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileDataWebDto;
import com.subtitlescorrector.core.service.corrections.SubtitlesCorrectionService;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitlesFileProcessor;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.JsonSerializationUtil;
import com.subtitlescorrector.core.util.Util;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubtitleTextCorrectionsItTest {

	private static final Logger log = LoggerFactory.getLogger(SubtitleTextCorrectionsItTest.class);
	
	@Autowired
	SrtSubtitlesFileProcessor processor;
	
	@Autowired
	SubtitlesCorrectionService correctionService;
	
	@MockBean
	RedisServiceAdapter redis;
	
	@MockBean
	WebSocketOutboundAdapter webSocketAdapter;
	
	@Autowired
	SubtitleFileProviderForUserServiceImpl fileProvider;

	private File testFile;

	/**
	 * 1. Correction
	 * 1.0 For ALL subtitle formats
	 * 1.1 File without BOM, with b,i,u,font html tags and with æ, Æ, è and È. 
	 *   Validate if html tags are stripped and invalid characters replaced.
	 * 1.2 File with BOM, with b,i,u,font html tags and with æ, Æ, è and È. 
	 *   Validate if html tags are stripped and invalid characters replaced.
	 * 1.3 File with BOM, when remove BOM, validate if BOM is removed
	 * 1.4 File with BOM, when keep BOM, validate if BOM is kept
	 * 1.5 File with multiple BOMs, when remove BOM, validate if all BOMs are removed
	 * 1.6 File with multiple BOMs, when keep BOM, validate that only 1 BOM is kept
	 * 1.7 File when uploaded and subtitles shifted, validate if shifted correctly
	 * 1.8 File when uploaded and edited in editor, check if saves file with edits
	 * 1.9 AI corrections for all formats
	 * 1.10 Srt file with invalid timestamp delimiters should be fixed
	 * 1.11 Srt file with multiple consecutive blank lines at the beginning
	 * 1.12 Srt file with multiple consecutive blank lines in the middle (TBA)
	 * 1.13 File with other then UTF-8 encoding should be converted to UTF-8
	 * 1.14 Vtt file with header, confirm it stays valid and contains header after correction
	 * 1.15 Vtt file without header, confirm valid after correction
	 * 1.16 Validate big file gets rejected
	 * 1.17 Validate number of requests exceeded rejects upload
	 * 1.18 Validate contact form on homepage works correctly
	 * 1.19 Validate feedback form after download works correctly
	 * 
	 * 2. Conversion
	 * 2.1 Srt -> Vtt
	 * 2.2 Srt with BOM -> Vtt, should keep BOM
	 * 2.3 Srt -> Ass
	 * 2.4 Srt with BOM -> Ass, should keep BOM
	 * 2.5 Vtt -> Srt
	 * 2.6 Vtt with BOM -> Srt, should keep BOM
	 * 2.7 Vtt -> Ass
	 * 2.8 Vtt with BOM -> Ass, should keep BOM
	 * 2.9 Ass -> Srt
	 * 2.10 Ass with BOM -> Srt, should keep BOM
	 * 2.11 Ass -> Vtt
	 * 2.12 Ass with BOM -> Vtt, should keep BOM
	 * 
	 * 
	 * 
	 */
	
	@BeforeAll
	public void setUp() {
		
		doAnswer(invocation -> {
            String message = invocation.getArgument(0);
            String webSocketSessionId = invocation.getArgument(1);
            log.info("Mock sending message to web socket! [" + message + "," + webSocketSessionId + "]");
            return null;
        }).when(webSocketAdapter).sendMessage(anyString(), nullable(String.class));

	}
	
	@Test
	void givenSubtitleFileWithoutBOMWithCyrilicInvalidLatinCharactersAndHtmlTags_whenProcessingWithStripHtmlAndFixCharactersOptions_thenFixCyrilicCharactersAndStripHtml() {

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle_files/srt/subtitle_withoutBOM_withHtmlTags_withInvalidLatinCyrilicCharacters.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AdditionalData data;
		data = new AdditionalData();
		data.setConvertAeToTj(true);
		data.setConvertAEToTJ(true);
		data.setConverteToch(true);
		data.setConvertEToCH(true);
		
		data.setStripBTags(true);
		data.setStripFontTags(true);
		data.setStripITags(true);
		data.setStripUTags(true);
		
		data.setAiEnabled(false);
		
		SrtSubtitleFileData fileData = processor.process(testFile, FileUtil.loadTextFile(testFile), data, null);
		
		assertEquals(fileData.getDetectedCharset(), StandardCharsets.UTF_8);
		assertEquals(fileData.getLines().size(), 8);
		
		assertEquals(fileData.getLines().get(0).getText(), "aaaaaaaaaaaa not permited tag<br>aaaaaaaaaaaa permited tag");
		assertEquals(fileData.getLines().get(0).getNumber(), 1);
		assertEquals(fileData.getLines().get(0).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(0).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(0).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(1).getText(), "multi<br>line");
		assertEquals(fileData.getLines().get(1).getNumber(), 2);
		assertEquals(fileData.getLines().get(1).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(1).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(1).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(2).getText(), "Ћирилица");
		assertEquals(fileData.getLines().get(2).getNumber(), 3);
		assertEquals(fileData.getLines().get(2).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(2).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(2).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(3).getText(), "Ćirilica");
		assertEquals(fileData.getLines().get(3).getNumber(), 4);
		assertEquals(fileData.getLines().get(3).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(3).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(3).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(4).getText(), "asdasdasdi tag u tag b tag");
		assertEquals(fileData.getLines().get(4).getNumber(), 5);
		assertEquals(fileData.getLines().get(4).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(4).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(4).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(5).getText(), "Nemilosrdna je prema svemu<br>lažnom. Ali zato je i predivna.");
		assertEquals(fileData.getLines().get(5).getNumber(), 6);
		assertEquals(fileData.getLines().get(5).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(5).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(5).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(6).getText(), "test");
		assertEquals(fileData.getLines().get(6).getNumber(), 7);
		assertEquals(fileData.getLines().get(6).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(6).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(6).getTimestampTo() != null);
		
		assertEquals(fileData.getLines().get(7).getText(), "ćĆčČ");
		assertEquals(fileData.getLines().get(7).getNumber(), 8);
		assertEquals(fileData.getLines().get(7).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(7).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(7).getTimestampTo() != null);

		


	}

	@Test
	void givenSubtitleFileWithoutBOMWithCyrilicInvalidLatinCharactersAndHtmlTags_whenProcessingWithoutStripHtmlTagsOption_thenStripForbiddenAndLeavePermittedTags() {

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle_files/srt/subtitle_withoutBOM_withHtmlTags_withInvalidLatinCyrilicCharacters.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AdditionalData data;
		data = new AdditionalData();
		
		data.setAiEnabled(false);
		
		SrtSubtitleFileData fileData = processor.process(testFile, FileUtil.loadTextFile(testFile), data, null);
		
		assertEquals(fileData.getDetectedCharset(), StandardCharsets.UTF_8);
		assertEquals(fileData.getLines().size(), 8);
		
		assertEquals(fileData.getLines().get(0).getText(), "aaaaaaaaaaaa not permited tag<br>aaaaaaaaaaaa <font color=\"red\">permited tag</font>");
		assertEquals(fileData.getLines().get(0).getNumber(), 1);
		assertEquals(fileData.getLines().get(0).getFormat(), SubtitleFormat.SRT);
		assertTrue(fileData.getLines().get(0).getTimestampFrom() != null);
		assertTrue(fileData.getLines().get(0).getTimestampTo() != null);
	}
	
	
	@Test
	void givenSubtitleFileWithBOMWithCyrilicInvalidLatinCharactersAndHtmlTags_whenProcessingWithStripHtmlAndFixCharactersOptions_thenFixCyrilicCharactersAndStripHtml() {

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle_files/srt/subtitle_withBOM_withHtmlTags_withInvalidLatinCyrilicCharacters.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AdditionalData data;
		data = new AdditionalData();
		data.setConvertAeToTj(true);
		data.setConvertAEToTJ(true);
		data.setConverteToch(true);
		data.setConvertEToCH(true);
		
		data.setStripBTags(true);
		data.setStripFontTags(true);
		data.setStripITags(true);
		data.setStripUTags(true);
		
		data.setAiEnabled(false);
		
		SubtitleCorrectionFileDataWebDto fileDtoData = correctionService.applyCorrectionOperations(data, testFile);
		
		assertEquals(fileDtoData.getLines().size(), 8);
		
		assertEquals(fileDtoData.getLines().get(0).getText(), "aaaaaaaaaaaa not permited tag<br>aaaaaaaaaaaa permited tag");
		assertEquals(fileDtoData.getLines().get(0).getNumber(), 1);
		assertTrue(fileDtoData.getLines().get(0).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(0).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(1).getText(), "multi<br>line");
		assertEquals(fileDtoData.getLines().get(1).getNumber(), 2);
		assertTrue(fileDtoData.getLines().get(1).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(1).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(2).getText(), "Ћирилица");
		assertEquals(fileDtoData.getLines().get(2).getNumber(), 3);
		assertTrue(fileDtoData.getLines().get(2).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(2).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(3).getText(), "Ćirilica");
		assertEquals(fileDtoData.getLines().get(3).getNumber(), 4);
		assertTrue(fileDtoData.getLines().get(3).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(3).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(4).getText(), "asdasdasdi tag u tag b tag");
		assertEquals(fileDtoData.getLines().get(4).getNumber(), 5);
		assertTrue(fileDtoData.getLines().get(4).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(4).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(5).getText(), "Nemilosrdna je prema svemu<br>lažnom. Ali zato je i predivna.");
		assertEquals(fileDtoData.getLines().get(5).getNumber(), 6);
		assertTrue(fileDtoData.getLines().get(5).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(5).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(6).getText(), "test");
		assertEquals(fileDtoData.getLines().get(6).getNumber(), 7);
		assertTrue(fileDtoData.getLines().get(6).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(6).getTimestampTo() != null);
		
		assertEquals(fileDtoData.getLines().get(7).getText(), "ćĆčČ");
		assertEquals(fileDtoData.getLines().get(7).getNumber(), 8);
		assertTrue(fileDtoData.getLines().get(7).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(7).getTimestampTo() != null);

		


	}

	@Test
	void givenSubtitleFileWithBOMWithCyrilicInvalidLatinCharactersAndHtmlTags_whenProcessingWithoutStripHtmlTagsOption_thenStripForbiddenAndLeavePermittedTags() {

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle_files/srt/subtitle_withBOM_withHtmlTags_withInvalidLatinCyrilicCharacters.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AdditionalData data;
		data = new AdditionalData();
		
		data.setAiEnabled(false);
		
		SubtitleCorrectionFileDataWebDto fileDtoData = correctionService.applyCorrectionOperations(data, testFile);
		
		assertEquals(fileDtoData.getLines().size(), 8);
		
		assertEquals(fileDtoData.getLines().get(0).getText(), "aaaaaaaaaaaa not permited tag<br>aaaaaaaaaaaa <font color=\"red\">permited tag</font>");
		assertEquals(fileDtoData.getLines().get(0).getNumber(), 1);
		assertTrue(fileDtoData.getLines().get(0).getTimestampFrom() != null);
		assertTrue(fileDtoData.getLines().get(0).getTimestampTo() != null);
	}
	
	@Test
	void givenSubtitleFileWithBOM_whenProcessingWithKeepBOMFlagFalse_thenRemoveBOM() {

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle_files/srt/subtitle_withBOM_withHtmlTags_withInvalidLatinCyrilicCharacters.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AdditionalData data;
		data = new AdditionalData();
		
		data.setAiEnabled(false);
		data.setKeepBOM(false);
		
		SubtitleCorrectionFileDataWebDto fileDtoData = correctionService.applyCorrectionOperations(data, testFile);
		
		assertEquals(false,fileDtoData.getLines().get(0).getText().startsWith("\uFEFF"));
	}
	
	@Test
	void givenSubtitleFileWithBOM_whenProcessingWithKeepBOMFlagTrue_thenKeepBOM() {

		InputStream is;
		try {
			is = new ClassPathResource("test_resources/subtitle_files/srt/subtitle_withBOM_withHtmlTags_withInvalidLatinCyrilicCharacters.srt").getInputStream();
			testFile = Util.inputStreamToFile(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AdditionalData data;
		data = new AdditionalData();
		
		data.setAiEnabled(false);
		
		UserSubtitleCorrectionCurrentVersionMetadata metadata = new UserSubtitleCorrectionCurrentVersionMetadata();
		BomData bom = new BomData();
		bom.setHasBom(true);
		bom.setKeepBom(true);
		metadata.setBomData(bom);
		metadata.setFilename("filename");
		metadata.setFormat(SubtitleFormat.SRT);
		
		SrtSubtitleFileData fileData = processor.process(testFile, FileUtil.loadTextFile(testFile), data, null);
		when(redis.getUserSubtitleCurrentVersionJson(anyString())).thenReturn(JsonSerializationUtil.srtSubtitleFileDataToJson(fileData));
		when(redis.getUsersLastUpdatedSubtitleFileMetadata(anyString())).thenReturn(metadata);
		
		UserSubtitleData userData = fileProvider.provideFileForUser("foo");
		boolean hasBom = FileUtil.loadTextFile(userData.getFile()).get(0).startsWith("\uFEFF");
		assertTrue(hasBom);
	}

}
