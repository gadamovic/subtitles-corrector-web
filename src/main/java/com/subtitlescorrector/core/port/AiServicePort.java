package com.subtitlescorrector.core.port;

import com.subtitlescorrector.core.domain.ai.ChatResponse;

public interface AiServicePort {

	ChatResponse askOpenAi(String prompt);

}