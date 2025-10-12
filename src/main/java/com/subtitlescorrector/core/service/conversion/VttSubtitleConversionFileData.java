package com.subtitlescorrector.core.service.conversion;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleFileData;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleUnitData;

public class VttSubtitleConversionFileData implements Serializable{

	private static final long serialVersionUID = -2406499811554095114L;

	private String filename;
	
	private List<VttSubtitleUnitData> lines;
	
	private SubtitleFormat sourceFormat;
	
	private SubtitleFormat targetFormat;
	
	private String detectedEncoding;
	
	private BomData bomData;
	
	/**
	 * Everything that goes above the start of subtitle lines
	 */
	List<String> header;


	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<VttSubtitleUnitData> getLines() {
		return lines;
	}

	public void setLines(List<VttSubtitleUnitData> lines) {
		this.lines = lines;
	}

	public SubtitleFormat getSourceFormat() {
		return sourceFormat;
	}

	public void setSourceFormat(SubtitleFormat sourceFormat) {
		this.sourceFormat = sourceFormat;
	}

	public SubtitleFormat getTargetFormat() {
		return targetFormat;
	}

	public void setTargetFormat(SubtitleFormat targetFormat) {
		this.targetFormat = targetFormat;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDetectedEncoding() {
		return detectedEncoding;
	}

	public void setDetectedEncoding(String detectedEncoding) {
		this.detectedEncoding = detectedEncoding;
	}

	public BomData getBomData() {
		return bomData;
	}

	public void setBomData(BomData bomData) {
		this.bomData = bomData;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}
	
	public VttSubtitleFileData toSubtitleFileData() {

		VttSubtitleFileData vttFileData = new VttSubtitleFileData();

		vttFileData.setBomData(bomData);
		vttFileData.setDetectedCharset(Charset.forName(detectedEncoding));
		vttFileData.setFilename(filename);
		vttFileData.setLines(lines);
		vttFileData.setFormat(sourceFormat);
		vttFileData.setHeader(header);

		return vttFileData;

	}
	
}