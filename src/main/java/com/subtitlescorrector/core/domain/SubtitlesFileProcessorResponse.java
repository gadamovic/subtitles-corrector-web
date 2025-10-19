package com.subtitlescorrector.core.domain;

import java.io.File;
import java.util.List;

import com.subtitlescorrector.core.domain.srt.SrtSubtitleFileData;

public class SubtitlesFileProcessorResponse {

	SubtitlesProcessingStatus status;
	String downloadUrl;
	File file;
	SrtSubtitleFileData data;
	
	public SubtitlesProcessingStatus getStatus() {
		return status;
	}
	public void setStatus(SubtitlesProcessingStatus status) {
		this.status = status;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public SrtSubtitleFileData getData() {
		return data;
	}
	public void setData(SrtSubtitleFileData data) {
		this.data = data;
	}

}
