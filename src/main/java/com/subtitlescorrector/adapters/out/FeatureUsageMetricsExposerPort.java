package com.subtitlescorrector.adapters.out;

import com.subtitlescorrector.core.domain.metrics.Feature;

public interface FeatureUsageMetricsExposerPort {

	void track(Feature feature);

}