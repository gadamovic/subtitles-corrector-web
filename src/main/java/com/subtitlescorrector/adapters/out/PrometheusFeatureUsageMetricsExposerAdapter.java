package com.subtitlescorrector.adapters.out;

import org.springframework.stereotype.Component;

import com.subtitlescorrector.core.domain.metrics.Feature;

import io.micrometer.core.instrument.MeterRegistry;

@Component
public class PrometheusFeatureUsageMetricsExposerAdapter implements FeatureUsageMetricsExposerPort {

	private MeterRegistry registry;
	
	@Override
	public void track(Feature feature) {
		registry.counter("app_feature_usage_total", "feature", feature.getName())
        .increment();
	}
	
}
