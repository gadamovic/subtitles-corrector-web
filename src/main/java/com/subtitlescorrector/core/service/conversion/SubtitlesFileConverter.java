package com.subtitlescorrector.core.service.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleUnitData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleUnitData;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.vtt.domain.VttSubtitleUnitData;

@Service
public class SubtitlesFileConverter {

	@Autowired
	SrtSubtitleLinesToSubtitleUnitDataParser srtParser;

	@Autowired
	VttSubtitleLinesToSubtitleUnitDataParser vttParser;

	@Autowired
	AssSubtitleLinesToSubtitleUnitDataParser assParser;

	public List<String> convertAndReturnTextLines(SrtSubtitleConversionFileData srtData, SubtitleFormat targetFormat) {

		switch (targetFormat) {
		case SRT:
			return srtParser.convertToListOfStrings(srtData.getLines(), srtData.getBomData().getHasBom());
		case VTT:
			VttSubtitleConversionFileData vttData = convertSrtToVtt(srtData);
			return vttParser.convertToListOfStrings(vttData.getLines(), vttData.getBomData().getHasBom());
		case ASS:
			AssSubtitleConversionFileData assData = convertSrtToAss(srtData);
			return assParser.convertToListOfStrings(assData.getLines(), assData.getBomData().getHasBom());
		}

		return Collections.emptyList();
	}

	public List<String> convertAndReturnTextLines(VttSubtitleConversionFileData vttData, SubtitleFormat targetFormat) {

		switch (targetFormat) {
		case SRT:
			SrtSubtitleConversionFileData srtData = convertVttToSrt(vttData);
			return srtParser.convertToListOfStrings(srtData.getLines(), srtData.getBomData().getHasBom());
		case VTT:
			return vttParser.convertToListOfStrings(vttData.getLines(), vttData.getBomData().getHasBom());
		case ASS:
			AssSubtitleConversionFileData assData = convertVttToAss(vttData);
			return assParser.convertToListOfStrings(assData.getLines(), assData.getBomData().getHasBom());
		}
		
		return Collections.emptyList();

	}

	public List<String> convertAndReturnTextLines(AssSubtitleConversionFileData assData, SubtitleFormat targetFormat) {

		switch (targetFormat) {
		case SRT:
			SrtSubtitleConversionFileData srtData = convertAssToSrt(assData);
			return srtParser.convertToListOfStrings(srtData.getLines(), srtData.getBomData().getHasBom());
		case VTT:
			VttSubtitleConversionFileData vttData = convertAssToVtt(assData);
			return vttParser.convertToListOfStrings(vttData.getLines(), vttData.getBomData().getHasBom());
		case ASS:
			return assParser.convertToListOfStrings(assData.getLines(), assData.getBomData().getHasBom());
		}

		return Collections.emptyList();
		
	}

	private VttSubtitleConversionFileData convertSrtToVtt(SrtSubtitleConversionFileData srtData) {

		VttSubtitleConversionFileData data = new VttSubtitleConversionFileData();

		data.setBomData(srtData.getBomData());
		data.setDetectedEncoding(srtData.getDetectedEncoding());
		data.setFilename(srtData.getFilename());
		data.setSourceFormat(srtData.getSourceFormat());
		data.setTargetFormat(srtData.getTargetFormat());

		List<VttSubtitleUnitData> vttLines = new ArrayList<>();

		for (SrtSubtitleUnitData line : srtData.getLines()) {

			VttSubtitleUnitData vttLine = new VttSubtitleUnitData();
			vttLine.setCompEditOperations(line.getCompEditOperations());
			vttLine.setEditOperations(line.getEditOperations());
			vttLine.setFormat(line.getFormat());
			vttLine.setNumber(line.getNumber());
			vttLine.setText(line.getText());
			vttLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			vttLine.setTimestampFrom(line.getTimestampFrom());
			vttLine.setTimestampFromShifted(line.getTimestampFromShifted());
			vttLine.setTimestampTo(line.getTimestampTo());
			vttLine.setTimestampToShifted(line.getTimestampToShifted());

			vttLines.add(vttLine);
		}

		data.setLines(vttLines);

		return data;
	}

	private AssSubtitleConversionFileData convertSrtToAss(SrtSubtitleConversionFileData srtData) {

		AssSubtitleConversionFileData data = new AssSubtitleConversionFileData();

		data.setBomData(srtData.getBomData());
		data.setDetectedEncoding(srtData.getDetectedEncoding());
		data.setFilename(srtData.getFilename());
		data.setSourceFormat(srtData.getSourceFormat());
		data.setTargetFormat(srtData.getTargetFormat());

		List<AssSubtitleUnitData> assLines = new ArrayList<>();

		for (SrtSubtitleUnitData line : srtData.getLines()) {

			AssSubtitleUnitData assLine = new AssSubtitleUnitData();
			assLine.setCompEditOperations(line.getCompEditOperations());
			assLine.setEditOperations(line.getEditOperations());
			assLine.setFormat(line.getFormat());
			assLine.setNumber(line.getNumber());
			assLine.setText(line.getText());
			assLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			assLine.setTimestampFrom(line.getTimestampFrom());
			assLine.setTimestampFromShifted(line.getTimestampFromShifted());
			assLine.setTimestampTo(line.getTimestampTo());
			assLine.setTimestampToShifted(line.getTimestampToShifted());

			assLines.add(assLine);
		}

		data.setLines(assLines);

		return data;
	}

	private SrtSubtitleConversionFileData convertVttToSrt(VttSubtitleConversionFileData vttData) {

		SrtSubtitleConversionFileData data = new SrtSubtitleConversionFileData();

		data.setBomData(vttData.getBomData());
		data.setDetectedEncoding(vttData.getDetectedEncoding());
		data.setFilename(vttData.getFilename());
		data.setSourceFormat(vttData.getSourceFormat());
		data.setTargetFormat(vttData.getTargetFormat());

		List<SrtSubtitleUnitData> srtLines = new ArrayList<>();

		for (VttSubtitleUnitData line : vttData.getLines()) {

			SrtSubtitleUnitData srtLine = new SrtSubtitleUnitData();
			srtLine.setCompEditOperations(line.getCompEditOperations());
			srtLine.setEditOperations(line.getEditOperations());
			srtLine.setFormat(line.getFormat());
			srtLine.setNumber(line.getNumber());
			srtLine.setText(line.getText());
			srtLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			srtLine.setTimestampFrom(line.getTimestampFrom());
			srtLine.setTimestampFromShifted(line.getTimestampFromShifted());
			srtLine.setTimestampTo(line.getTimestampTo());
			srtLine.setTimestampToShifted(line.getTimestampToShifted());

			srtLines.add(srtLine);
		}

		data.setLines(srtLines);

		return data;

	}

	private AssSubtitleConversionFileData convertVttToAss(VttSubtitleConversionFileData vttData) {

		AssSubtitleConversionFileData data = new AssSubtitleConversionFileData();

		data.setBomData(vttData.getBomData());
		data.setDetectedEncoding(vttData.getDetectedEncoding());
		data.setFilename(vttData.getFilename());
		data.setSourceFormat(vttData.getSourceFormat());
		data.setTargetFormat(vttData.getTargetFormat());

		List<AssSubtitleUnitData> assLines = new ArrayList<>();

		for (VttSubtitleUnitData line : vttData.getLines()) {

			AssSubtitleUnitData assLine = new AssSubtitleUnitData();
			assLine.setCompEditOperations(line.getCompEditOperations());
			assLine.setEditOperations(line.getEditOperations());
			assLine.setFormat(line.getFormat());
			assLine.setNumber(line.getNumber());
			assLine.setText(line.getText());
			assLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			assLine.setTimestampFrom(line.getTimestampFrom());
			assLine.setTimestampFromShifted(line.getTimestampFromShifted());
			assLine.setTimestampTo(line.getTimestampTo());
			assLine.setTimestampToShifted(line.getTimestampToShifted());

			assLines.add(assLine);
		}

		data.setLines(assLines);

		return data;

	}

	private SrtSubtitleConversionFileData convertAssToSrt(AssSubtitleConversionFileData assData) {

		SrtSubtitleConversionFileData data = new SrtSubtitleConversionFileData();

		data.setBomData(assData.getBomData());
		data.setDetectedEncoding(assData.getDetectedEncoding());
		data.setFilename(assData.getFilename());
		data.setSourceFormat(assData.getSourceFormat());
		data.setTargetFormat(assData.getTargetFormat());

		List<SrtSubtitleUnitData> srtLines = new ArrayList<>();

		for (AssSubtitleUnitData line : assData.getLines()) {

			SrtSubtitleUnitData srtLine = new SrtSubtitleUnitData();
			srtLine.setCompEditOperations(line.getCompEditOperations());
			srtLine.setEditOperations(line.getEditOperations());
			srtLine.setFormat(line.getFormat());
			srtLine.setNumber(line.getNumber());
			srtLine.setText(line.getText());
			srtLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			srtLine.setTimestampFrom(line.getTimestampFrom());
			srtLine.setTimestampFromShifted(line.getTimestampFromShifted());
			srtLine.setTimestampTo(line.getTimestampTo());
			srtLine.setTimestampToShifted(line.getTimestampToShifted());

			srtLines.add(srtLine);
		}

		data.setLines(srtLines);

		return data;

	}

	private VttSubtitleConversionFileData convertAssToVtt(AssSubtitleConversionFileData assData) {

		VttSubtitleConversionFileData data = new VttSubtitleConversionFileData();

		data.setBomData(assData.getBomData());
		data.setDetectedEncoding(assData.getDetectedEncoding());
		data.setFilename(assData.getFilename());
		data.setSourceFormat(assData.getSourceFormat());
		data.setTargetFormat(assData.getTargetFormat());

		List<VttSubtitleUnitData> vttLines = new ArrayList<>();

		for (AssSubtitleUnitData line : assData.getLines()) {

			VttSubtitleUnitData vttLine = new VttSubtitleUnitData();
			vttLine.setCompEditOperations(line.getCompEditOperations());
			vttLine.setEditOperations(line.getEditOperations());
			vttLine.setFormat(line.getFormat());
			vttLine.setNumber(line.getNumber());
			vttLine.setText(line.getText());
			vttLine.setTextBeforeCorrection(line.getTextBeforeCorrection());
			vttLine.setTimestampFrom(line.getTimestampFrom());
			vttLine.setTimestampFromShifted(line.getTimestampFromShifted());
			vttLine.setTimestampTo(line.getTimestampTo());
			vttLine.setTimestampToShifted(line.getTimestampToShifted());

			vttLines.add(vttLine);
		}

		data.setLines(vttLines);

		return data;
		
	}

}
