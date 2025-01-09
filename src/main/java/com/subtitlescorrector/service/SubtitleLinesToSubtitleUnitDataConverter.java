package com.subtitlescorrector.service;

import java.util.List;

import com.subtitlescorrector.domain.SubtitleUnitData;

public interface SubtitleLinesToSubtitleUnitDataConverter {

	List<SubtitleUnitData> convert(List<String> lines);

}