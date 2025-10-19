package com.subtitlescorrector.core.service.converters;

import java.util.List;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.srt.SrtSubtitleUnitData;

public interface SubtitleLinesToSubtitleUnitDataConverter {

	List<SrtSubtitleUnitData> convertToSubtitleUnits(List<String> lines);
	public List<String> convertToListOfStrings(List<SrtSubtitleUnitData> lines, boolean addBom);

}