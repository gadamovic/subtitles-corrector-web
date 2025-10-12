package com.subtitlescorrector.core.service.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitleLinesToSubtitleUnitDataParser;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitleLinesToSubtitleUnitDataParser;

@Service
public class SubtitlesConverterFactory {

//	@Autowired
//	SrtSubtitleLinesToSubtitleUnitDataConverter srtConverter;
//
//	@Autowired
//	VttSubtitleLinesToSubtitleUnitDataConverter vttConverter;
//	
//	@Autowired
//	AssSubtitleLinesToSubtitleUnitDataConverter assConverter;
//
//	public SubtitleLinesToSubtitleUnitDataConverter getConverter(SubtitleFormat format) {
//		switch (format) {
//		case SRT:
//			return srtConverter;
//		case VTT:
//			return vttConverter;
//		case ASS:
//			return assConverter;
//		default:
//			return null;
//		}
//	}

}
