package com.subtitlescorrector.core.domain;

import java.io.File;

public class UserSubtitleConversionData {

	private SubtitleConversionFileData data;
	private File file;
	public SubtitleConversionFileData getData() {
		return data;
	}
	public void setData(SubtitleConversionFileData data) {
		this.data = data;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
