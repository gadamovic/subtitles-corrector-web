package com.subtitlescorrector.core.service.corrections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.SubtitleFormat;
import com.subtitlescorrector.core.service.corrections.ass.AssSubtitlesFileProcessor;
import com.subtitlescorrector.core.service.corrections.srt.SrtSubtitlesFileProcessor;
import com.subtitlescorrector.core.service.corrections.vtt.VttSubtitlesFileProcessor;

@Service
public class SubtitlesProcessorFactory {

	@Autowired
	SrtSubtitlesFileProcessor srtProcessor;
	
	@Autowired
	VttSubtitlesFileProcessor vttProcessor;
	
	@Autowired
	AssSubtitlesFileProcessor assProcessor;
	
	public Object getProcessor(SubtitleFormat format) {
		
		switch(format) {
		case SRT: return srtProcessor;
		case VTT: return vttProcessor;
		case ASS: return assProcessor;
		default: return null;
		}
		
	}
	
}
