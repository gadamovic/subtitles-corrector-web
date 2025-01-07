package com.subtitlescorrector.domain;

import java.io.File;
import java.util.List;

public class SubtitlesFileProcessorResponse {

	SubtitlesProcessingStatus status;
	String downloadUrl;
	File file;
	List<String> lines;
	
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
	public List<String> getLines() {
		return lines;
	}
	public void setLines(List<String> lines) {
		this.lines = lines;
	}

}
