package com.subtitlescorrector.core.service.corrections.vtt.domain;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.subtitlescorrector.core.domain.BomData;
import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.service.conversion.VttSubtitleConversionFileData;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileDataWebDto;
import com.subtitlescorrector.core.service.corrections.SubtitleCorrectionFileLineDataWebDto;

public class VttSubtitleFileData implements Serializable{

	private static final long serialVersionUID = -2406499811554095114L;

	String filename;
	
	List<VttSubtitleUnitData> lines;
	
	String httpResponseMessage;
	
	Charset detectedCharset;
	
	SubtitleFormat format;
	
	BomData bomData;
	
	/**
	 * Everything that goes above the start of subtitle lines
	 */
	List<String> header;
	
	public SubtitleFormat getFormat() {
		return format;
	}

	public void setFormat(SubtitleFormat format) {
		this.format = format;
	}

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

	public String getHttpResponseMessage() {
		return httpResponseMessage;
	}

	public void setHttpResponseMessage(String httpResponseMessage) {
		this.httpResponseMessage = httpResponseMessage;
	}

	public Charset getDetectedCharset() {
		return detectedCharset;
	}

	public void setDetectedCharset(Charset detectedCharset) {
		this.detectedCharset = detectedCharset;
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
	
	public void addLineToHeader(String line) {
		if(this.header == null) {
			this.header = new ArrayList<>();
		}
		
		this.header.add(line);
	}

	public List<SubtitleCorrectionFileLineDataWebDto> linesToResponseLines(){
		
		List<SubtitleCorrectionFileLineDataWebDto> response = new ArrayList<>();
		
		for(VttSubtitleUnitData line : lines) {
			
			SubtitleCorrectionFileLineDataWebDto responseLine = new SubtitleCorrectionFileLineDataWebDto();
			responseLine.setCompEditOperations(line.getCompEditOperations());
			responseLine.setNumber(line.getNumber());
			responseLine.setText(line.getText());
			responseLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			responseLine.setTimestampFrom(line.getTimestampFrom());
			responseLine.setTimestampFromShifted(line.getTimestampFromShifted());
			responseLine.setTimestampTo(line.getTimestampTo());
			responseLine.setTimestampToShifted(line.getTimestampToShifted());
			response.add(responseLine);
		}
		
		return response;
		
	}
	
	public void merge(SubtitleCorrectionFileDataWebDto subtitleData) {
		
		List<SubtitleCorrectionFileLineDataWebDto> dtoLines = subtitleData.getLines();
		
		for(int i = 0; i < lines.size(); i ++) {
			
			lines.get(i).setTimestampFrom(dtoLines.get(i).getTimestampFrom());
			lines.get(i).setTimestampFromShifted(dtoLines.get(i).getTimestampFromShifted());
			lines.get(i).setTimestampTo(dtoLines.get(i).getTimestampTo());
			lines.get(i).setTimestampToShifted(dtoLines.get(i).getTimestampToShifted());
			lines.get(i).setText(dtoLines.get(i).getText());
			lines.get(i).setTextBeforeCorrection(dtoLines.get(i).getTextBeforeCorrection());
			lines.get(i).setCompEditOperations(dtoLines.get(i).getCompEditOperations());
			
		}
		
	}

	public VttSubtitleConversionFileData toSubtitleFileConversionData() {
		
		VttSubtitleConversionFileData vttConversionData = new VttSubtitleConversionFileData();
		
		vttConversionData.setBomData(bomData);
		vttConversionData.setDetectedEncoding(detectedCharset.displayName());
		vttConversionData.setFilename(filename);
		vttConversionData.setLines(lines);
		vttConversionData.setSourceFormat(format);
		vttConversionData.setHeader(header);
		
		return vttConversionData;
	}
	

}
