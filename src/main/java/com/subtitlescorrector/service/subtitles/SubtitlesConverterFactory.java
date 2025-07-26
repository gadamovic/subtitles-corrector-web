package com.subtitlescorrector.service.subtitles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.SubtitleFormat;

@Service
public class SubtitlesConverterFactory {

	@Autowired
	SrtSubtitleLinesToSubtitleUnitDataConverter srtConverter;

	@Autowired
	VttSubtitleLinesToSubtitleUnitDataConverter vttConverter;

	public SubtitleLinesToSubtitleUnitDataConverter getConverter(SubtitleFormat format) {
		switch (format) {
		case SRT:
			return srtConverter;
		case VTT:
			return vttConverter;
		default:
			return null;
		}
	}

}
