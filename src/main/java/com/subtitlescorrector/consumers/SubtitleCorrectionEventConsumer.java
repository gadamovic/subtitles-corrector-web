package com.subtitlescorrector.consumers;

import java.util.List;

import com.subtitlescorrector.generated.avro.SubtitleCorrectionEvent;

public interface SubtitleCorrectionEventConsumer {

	void consumeCorrections(List<SubtitleCorrectionEvent> events);

}