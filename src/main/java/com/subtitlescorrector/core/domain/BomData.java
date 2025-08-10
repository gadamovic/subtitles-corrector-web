package com.subtitlescorrector.core.domain;

public class BomData {

	/**
	 * Byte order mark
	 */
	protected Boolean hasBom;
	
	/**
	 * User selected option to keep bom or not
	 */
	protected Boolean keepBom;
	

	public Boolean getHasBom() {
		return hasBom;
	}

	public void setHasBom(Boolean hasBom) {
		this.hasBom = hasBom;
	}

	public Boolean getKeepBom() {
		return keepBom;
	}

	public void setKeepBom(Boolean keepBom) {
		this.keepBom = keepBom;
	}
	
}
