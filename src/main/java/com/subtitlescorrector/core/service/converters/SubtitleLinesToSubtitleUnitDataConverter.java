package com.subtitlescorrector.core.service.converters;

import java.util.List;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleUnitData;

public interface SubtitleLinesToSubtitleUnitDataConverter {

	List<SrtSubtitleUnitData> convertToSubtitleUnits(List<String> lines);
	public List<String> convertToListOfStrings(List<SrtSubtitleUnitData> lines, boolean addBom);

}