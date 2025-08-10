package com.subtitlescorrector.core.service.converters;

import java.util.List;

import com.subtitlescorrector.core.domain.AdditionalData;
import com.subtitlescorrector.core.domain.SubtitleUnitData;

public interface SubtitleLinesToSubtitleUnitDataConverter {

	List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines);
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines, boolean addBom);

}