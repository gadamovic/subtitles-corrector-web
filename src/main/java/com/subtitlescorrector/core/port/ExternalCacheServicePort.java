package com.subtitlescorrector.core.port;

import java.time.LocalDateTime;
import java.util.List;

import com.subtitlescorrector.core.domain.UserSubtitleConversionCurrentVersionMetadata;
import com.subtitlescorrector.core.domain.UserSubtitleCorrectionCurrentVersionMetadata;
import com.subtitlescorrector.core.service.conversion.AssSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.SrtSubtitleConversionFileData;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleUnitData;

public interface ExternalCacheServicePort {

	void updateLastS3UploadTimestamp(String ip);
	public LocalDateTime getLastS3UploadTimestamp(String ip);
	public void addWebSocketUserToCache(String userId, String webSocketSessionId);
	public String getWebSocketSessionIdForUser(String userId);
	public Integer incrementAndGetNumberOfEmailsInCurrentHour();
	public void addUserSubtitleCurrentVersion(String jsonData, String userId);
	public void addUserSubtitleConversionData(String data, String userId);
	public String getUserSubtitleCurrentVersionJson(String userId);
	public VttSubtitleConversionFileData getVttUserSubtitleConversionData(String userId);
	public AssSubtitleConversionFileData getAssUserSubtitleConversionData(String userId);
	public SrtSubtitleConversionFileData getSrtUserSubtitleConversionData(String userId);
	public Integer incrementAndGetNumberOfSubtitlesProcessedByUserInCurrentTimeInterval(String userId);
	public UserSubtitleCorrectionCurrentVersionMetadata getUsersLastUpdatedSubtitleFileMetadata(String userId);
	public void addUsersLastUpdatedSubtitleFileMetadata(UserSubtitleCorrectionCurrentVersionMetadata metadata, String userId);
	public void addUsersLastUpdatedSubtitleConversionFileMetadata(UserSubtitleConversionCurrentVersionMetadata metadata, String userId);
	public UserSubtitleConversionCurrentVersionMetadata getUsersLastUpdatedSubtitleConversionFileMetadata(String userId);
	
}