package com.subtitlescorrector.adapters.out;

import io.micrometer.core.instrument.Counter;

public interface FeatureUsageMetricsExposerPort {

	Counter getCorrectionsUploadsCounter();

	Counter getConversionsUploadsCounter();

}