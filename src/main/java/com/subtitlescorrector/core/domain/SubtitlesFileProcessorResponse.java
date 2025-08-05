package com.subtitlescorrector.core.domain;

import java.io.File;
import java.util.List;

public class SubtitlesFileProcessorResponse {

	SubtitlesProcessingStatus status;
	String downloadUrl;
	File file;
	SubtitleFileData data;
	
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
	public SubtitleFileData getData() {
		return data;
	}
	public void setData(SubtitleFileData data) {
		this.data = data;
	}

}
