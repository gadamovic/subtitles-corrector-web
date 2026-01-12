package com.subtitlescorrector.core.service.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.domain.SubtitleTimestamp;
import com.subtitlescorrector.core.domain.UserData;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.ass.AssSubtitleFileData;
import com.subtitlescorrector.core.domain.ass.AssSubtitleUnitData;
import com.subtitlescorrector.core.domain.deepl.DeepLResponse;
import com.subtitlescorrector.core.domain.deepl.DeepLTranslation;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.domain.translation.SubtitleTranslationDataResponse;
import com.subtitlescorrector.core.domain.translation.TranslationLanguage;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleFileData;
import com.subtitlescorrector.core.domain.vtt.VttSubtitleUnitData;
import com.subtitlescorrector.core.port.DeeplClientPort;
import com.subtitlescorrector.core.port.ExternalCacheServicePort;
import com.subtitlescorrector.core.port.SubtitlesCloudStoragePort;
import com.subtitlescorrector.core.service.DeepLUsageMetricsExposerService;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.util.FileUtil;
import com.subtitlescorrector.core.util.JsonSerializationUtil;
import com.subtitlescorrector.core.util.Util;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TranslationServiceTest {

	private static final String TRANSLATED = "translated";
	private static final String FILENAME = "filename";
	private static final String USER_ID = "userId";
	private static final String EMPTY_LANGUAGE_ERROR_MESSAGE = "Language must be provided!";
	private static final String SUBTITLE_LINE_TEXT = "testtext";
	private static final TranslationLanguage FRENCH = TranslationLanguage.FRENCH;

	@Mock
	UserData user;

	@Mock
	SubtitlesCloudStoragePort s3Service;

	@Mock
	DeeplClientPort deepLClient;

	@Mock
	ExternalCacheServicePort redisService;

	@Mock
	SrtSubtitleLinesToSubtitleUnitDataParser srtParser;

	@Mock
	VttSubtitleLinesToSubtitleUnitDataParser vttParser;

	@Mock
	AssSubtitleLinesToSubtitleUnitDataParser assParser;

	@Mock
	DeepLUsageMetricsExposerService usageMetricsExposer;

	@InjectMocks
	TranslationServiceImpl translationService;

	public void setUp() {

		when(user.getUserId()).thenReturn(USER_ID);
		doNothing().when(s3Service).storeIfProd(anyString(), any());

		when(deepLClient.translate(anyList(), anyString())).thenReturn(createDeeplTranslateResponse());

		doNothing().when(redisService).addUserSubtitleCurrentVersion(anyString(), anyString());
		doNothing().when(redisService).addUsersLastUpdatedSubtitleFileMetadata(any(), anyString());

		doNothing().when(usageMetricsExposer).reportUsage();

	}

	@Test
	public void givenSrtSubtitleFileAndTargetLanguage_whenTranslatingFile_translateCorrectly() {

		setUp();
		
		File subtitleFile = null;
		SubtitleTranslationDataResponse resp = null;
		
		when(srtParser.convertToSubtitleUnits(anyList())).thenReturn(createSrtSubtitleFileData());
		
		try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class)) {
			mocked.when(() -> Util.detectSubtitleFormat(anyList())).thenReturn(SubtitleFormat.SRT);

			try (MockedStatic<FileUtil> mockedFileUtil = Mockito.mockStatic(FileUtil.class)) {
				mockedFileUtil.when(() -> FileUtil.loadTextFile(any())).thenReturn(Collections.singletonList(SUBTITLE_LINE_TEXT));
				mockedFileUtil.when(() -> FileUtil.detectEncodingOfFile(any())).thenReturn(StandardCharsets.UTF_8);
				resp = translationService.translate(subtitleFile, FILENAME, FRENCH);

				mocked.verify(() -> Util.populateBomData(any(), eq(Collections.singletonList(SUBTITLE_LINE_TEXT))));

				verify(deepLClient).translate(Collections.singletonList(SUBTITLE_LINE_TEXT), FRENCH.getIsoCode());
				
				SrtSubtitleFileData translated = getTranslatedSrtFileData();
				verify(redisService).addUserSubtitleCurrentVersion(
						JsonSerializationUtil.srtSubtitleFileDataToJson(translated), USER_ID);
				
				verify(redisService).addUsersLastUpdatedSubtitleFileMetadata(
						  argThat(metadata -> 
					        metadata.getFilename().equals(FILENAME) &&
					        metadata.getFormat() == SubtitleFormat.SRT),
					      eq(USER_ID));
				
			}
		}

		assertEquals(StandardCharsets.UTF_8.displayName(), resp.getDetectedEncoding());
		assertEquals(FILENAME, resp.getFilename());
		assertEquals(SubtitleFormat.SRT, resp.getDetectedSourceFormat());
		assertEquals(1, resp.getNumberOfLines());
		
	}
	
	private SrtSubtitleFileData getTranslatedSrtFileData() {
		SrtSubtitleFileData translated = createSrtSubtitleFileData();
		translated.getLines().get(0).setText(TRANSLATED);
		return translated;
	}
	
	@Test
	public void givenVttSubtitleFileAndTargetLanguage_whenTranslatingFile_translateCorrectly() {

		setUp();
		
		File subtitleFile = null;
		SubtitleTranslationDataResponse resp = null;

		when(vttParser.convertToSubtitleUnits(anyList())).thenReturn(createVttSubtitleFileData());
		
		try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class)) {
			mocked.when(() -> Util.detectSubtitleFormat(anyList())).thenReturn(SubtitleFormat.VTT);

			try (MockedStatic<FileUtil> mockedFileUtil = Mockito.mockStatic(FileUtil.class)) {
				mockedFileUtil.when(() -> FileUtil.loadTextFile(any())).thenReturn(Collections.singletonList(SUBTITLE_LINE_TEXT));
				mockedFileUtil.when(() -> FileUtil.detectEncodingOfFile(any())).thenReturn(StandardCharsets.UTF_8);
				resp = translationService.translate(subtitleFile, FILENAME, FRENCH);

				mocked.verify(() -> Util.populateBomData(any(), eq(Collections.singletonList(SUBTITLE_LINE_TEXT))));

				verify(deepLClient).translate(Collections.singletonList(SUBTITLE_LINE_TEXT), FRENCH.getIsoCode());
				
				VttSubtitleFileData translated = getTranslatedVttFileData();
				verify(redisService).addUserSubtitleCurrentVersion(
						JsonSerializationUtil.vttSubtitleFileDataToJson(translated), USER_ID);
				
				verify(redisService).addUsersLastUpdatedSubtitleFileMetadata(
						  argThat(metadata -> 
					        metadata.getFilename().equals(FILENAME) &&
					        metadata.getFormat() == SubtitleFormat.VTT),
					      eq(USER_ID));
			}
		}
		
		assertEquals(StandardCharsets.UTF_8.displayName(), resp.getDetectedEncoding());
		assertEquals(FILENAME, resp.getFilename());
		assertEquals(SubtitleFormat.VTT, resp.getDetectedSourceFormat());
		assertEquals(1, resp.getNumberOfLines());

	}

	private VttSubtitleFileData getTranslatedVttFileData() {
		VttSubtitleFileData translated = createVttSubtitleFileData();
		translated.getLines().get(0).setText(TRANSLATED);
		return translated;
	}

	@Test
	public void givenAssSubtitleFileAndTargetLanguage_whenTranslatingFile_translateCorrectly() {

		setUp();
		
		File subtitleFile = null;
		SubtitleTranslationDataResponse resp = null;
		
		when(assParser.convertToSubtitleUnits(anyList())).thenReturn(createAssSubtitleFileData());
		
		try (MockedStatic<Util> mocked = Mockito.mockStatic(Util.class)) {
			mocked.when(() -> Util.detectSubtitleFormat(anyList())).thenReturn(SubtitleFormat.ASS);

			try (MockedStatic<FileUtil> mockedFileUtil = Mockito.mockStatic(FileUtil.class)) {
				mockedFileUtil.when(() -> FileUtil.loadTextFile(any())).thenReturn(Collections.singletonList(SUBTITLE_LINE_TEXT));
				mockedFileUtil.when(() -> FileUtil.detectEncodingOfFile(any())).thenReturn(StandardCharsets.UTF_8);
				resp = translationService.translate(subtitleFile, FILENAME, FRENCH);

				mocked.verify(() -> Util.populateBomData(any(), eq(Collections.singletonList(SUBTITLE_LINE_TEXT))));

				verify(deepLClient).translate(Collections.singletonList(SUBTITLE_LINE_TEXT), FRENCH.getIsoCode());
				
				AssSubtitleFileData translated = getTranslatedAssFileData();
				verify(redisService).addUserSubtitleCurrentVersion(
						JsonSerializationUtil.assSubtitleFileDataToJson(translated), USER_ID);
				
				verify(redisService).addUsersLastUpdatedSubtitleFileMetadata(
						  argThat(metadata -> 
					        metadata.getFilename().equals(FILENAME) &&
					        metadata.getFormat() == SubtitleFormat.ASS),
					      eq(USER_ID));
			}
		}
		
		assertEquals(StandardCharsets.UTF_8.displayName(), resp.getDetectedEncoding());
		assertEquals(FILENAME, resp.getFilename());
		assertEquals(SubtitleFormat.ASS, resp.getDetectedSourceFormat());
		assertEquals(1, resp.getNumberOfLines());

	}
	
	private AssSubtitleFileData getTranslatedAssFileData() {
		AssSubtitleFileData translated = createAssSubtitleFileData();
		translated.getLines().get(0).setText(TRANSLATED);
		return translated;
	}

	@Test
	public void givenFileWithNullLanguage_whenTranslatingFile_returnCorrectErrorMessage() {

		File subtitleFile = null;
		SubtitleTranslationDataResponse resp = null;

		resp = translationService.translate(subtitleFile, FILENAME, null);
		
		assertEquals(EMPTY_LANGUAGE_ERROR_MESSAGE, resp.getHttpResponseMessage());

	}
	
	private UserSubtitleCorrectionCurrentVersionMetadata createMetadata(SubtitleFormat sourceFormat) {
		UserSubtitleCorrectionCurrentVersionMetadata metadata = new UserSubtitleCorrectionCurrentVersionMetadata();
		metadata.setBomData(new BomData());
		metadata.setFilename(FILENAME);
		metadata.setFormat(sourceFormat);
		return metadata;
	}
	
	private SrtSubtitleFileData createSrtSubtitleFileData() {

		SrtSubtitleFileData srtFileData = new SrtSubtitleFileData();

		SrtSubtitleUnitData line = new SrtSubtitleUnitData();
		line.setFormat(SubtitleFormat.SRT);
		line.setNumber(5);
		line.setText(SUBTITLE_LINE_TEXT);
		line.setTextBeforeCorrection("somethingelse");

		SubtitleTimestamp ts = new SubtitleTimestamp();
		ts.setFormattedTimestamp("asdasdas");
		ts.setHour((short) 5);
		ts.setMillisecond((short) 123);
		ts.setMinute((short) 12);
		ts.setSecond((short) 11);

		line.setTimestampFrom(ts);
		line.setTimestampTo(ts);
		line.setTimestampFromShifted(ts);
		line.setTimestampToShifted(ts);

		BomData bom = createBom();

		srtFileData.setBomData(bom);
		srtFileData.setDetectedCharset(StandardCharsets.UTF_16);
		srtFileData.setFilename(FILENAME);
		srtFileData.setLines(Collections.singletonList(line));

		return srtFileData;
	}

	private BomData createBom() {
		BomData bom = new BomData();

		bom.setHasBom(true);
		bom.setKeepBom(true);
		return bom;
	}

	private VttSubtitleFileData createVttSubtitleFileData() {

		VttSubtitleFileData vttFileData = new VttSubtitleFileData();

		VttSubtitleUnitData line = new VttSubtitleUnitData();
		line.setFormat(SubtitleFormat.SRT);
		line.setNumber(5);
		line.setText(SUBTITLE_LINE_TEXT);
		line.setTextBeforeCorrection("somethingelse");

		SubtitleTimestamp ts = new SubtitleTimestamp();
		ts.setFormattedTimestamp("asdasdas");
		ts.setHour((short) 5);
		ts.setMillisecond((short) 123);
		ts.setMinute((short) 12);
		ts.setSecond((short) 11);

		line.setTimestampFrom(ts);
		line.setTimestampTo(ts);
		line.setTimestampFromShifted(ts);
		line.setTimestampToShifted(ts);

		BomData bom = createBom();

		vttFileData.setBomData(bom);
		vttFileData.setDetectedCharset(StandardCharsets.UTF_16);
		vttFileData.setFilename(FILENAME);
		vttFileData.setLines(Collections.singletonList(line));

		return vttFileData;
	}

	private AssSubtitleFileData createAssSubtitleFileData() {

		AssSubtitleFileData assFileData = new AssSubtitleFileData();

		AssSubtitleUnitData line = new AssSubtitleUnitData();
		line.setFormat(SubtitleFormat.SRT);
		line.setNumber(5);
		line.setText(SUBTITLE_LINE_TEXT);
		line.setTextBeforeCorrection("somethingelse");

		SubtitleTimestamp ts = new SubtitleTimestamp();
		ts.setFormattedTimestamp("asdasdas");
		ts.setHour((short) 5);
		ts.setMillisecond((short) 123);
		ts.setMinute((short) 12);
		ts.setSecond((short) 11);

		line.setTimestampFrom(ts);
		line.setTimestampTo(ts);
		line.setTimestampFromShifted(ts);
		line.setTimestampToShifted(ts);

		BomData bom = createBom();

		assFileData.setBomData(bom);
		assFileData.setDetectedCharset(StandardCharsets.UTF_16);
		assFileData.setFilename(FILENAME);
		assFileData.setLines(Collections.singletonList(line));

		return assFileData;
	}

	private DeepLResponse createDeeplTranslateResponse() {

		DeepLResponse resp = new DeepLResponse();
		DeepLTranslation translation = new DeepLTranslation();
		translation.setDetectedSourceLanguage("source language");
		translation.setText(TRANSLATED);
		resp.setTranslations(Collections.singletonList(translation));
		return resp;
	}

}
