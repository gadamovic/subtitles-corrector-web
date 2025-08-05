package com.subtitlescorrector.core.domain;

import java.io.File;

public class UserSubtitleData {

	private SubtitleFileData subtitleFileData;
	private File file;
	public SubtitleFileData getSubtitleFileData() {
		return subtitleFileData;
	}
	public void setSubtitleFileData(SubtitleFileData subtitleFileData) {
		this.subtitleFileData = subtitleFileData;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
}
