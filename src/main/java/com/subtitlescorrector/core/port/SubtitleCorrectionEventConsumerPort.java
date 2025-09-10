package com.subtitlescorrector.core.port;

import java.util.List;

import com.subtitlescorrector.core.domain.SubtitleCorrectionEvent;



public interface SubtitleCorrectionEventConsumerPort {

	void consumeCorrections(List<SubtitleCorrectionEvent> events);

}