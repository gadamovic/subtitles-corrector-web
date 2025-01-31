package com.subtitlescorrector.service.subtitles;

import java.util.List;

import com.subtitlescorrector.domain.AdditionalData;
import com.subtitlescorrector.domain.SubtitleUnitData;

public interface SubtitleLinesToSubtitleUnitDataConverter {

	List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines, AdditionalData params);
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines);

}