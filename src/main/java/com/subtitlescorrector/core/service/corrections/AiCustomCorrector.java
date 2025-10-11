package com.subtitlescorrector.core.service.corrections;

import java.util.List;

import com.subtitlescorrector.core.domain.ai.LineForAiCorrection;

public interface AiCustomCorrector {

	List<LineForAiCorrection> correct(List<LineForAiCorrection> lines, CorrectorParameters params);

}