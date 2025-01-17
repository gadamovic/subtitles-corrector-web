package com.subtitlescorrector.service.subtitles;

import java.util.List;

import com.subtitlescorrector.domain.SubtitleUnitData;

public interface SubtitleLinesToSubtitleUnitDataConverter {

	List<SubtitleUnitData> convertToSubtitleUnits(List<String> lines);
	public List<String> convertToListOfStrings(List<SubtitleUnitData> lines);

}