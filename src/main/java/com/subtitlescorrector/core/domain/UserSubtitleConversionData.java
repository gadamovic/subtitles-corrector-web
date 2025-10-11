package com.subtitlescorrector.core.domain;

import java.io.File;

public class UserSubtitleConversionData {

	private File file;
	private UserSubtitleConversionCurrentVersionMetadata metadata;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setMetadata(UserSubtitleConversionCurrentVersionMetadata metadata) {
		this.metadata = metadata;
	}
	public UserSubtitleConversionCurrentVersionMetadata getMetadata() {
		return metadata;
	}

}
