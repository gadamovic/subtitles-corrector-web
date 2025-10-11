package com.subtitlescorrector.core.domain;

import java.io.File;

import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;

public class UserSubtitleData {

	private UserSubtitleCorrectionCurrentVersionMetadata fileMetadata;
	private File file;

	public UserSubtitleCorrectionCurrentVersionMetadata getFileMetadata() {
		return fileMetadata;
	}
	public void setFileMetadata(UserSubtitleCorrectionCurrentVersionMetadata fileMetadata) {
		this.fileMetadata = fileMetadata;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
}
