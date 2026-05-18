package com.subtitlescorrector.adapters.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.core.domain.metrics.Feature;

import io.micrometer.core.instrument.MeterRegistry;

@Component
public class PrometheusFeatureUsageMetricsExposerAdapter implements FeatureUsageMetricsExposerPort {

	@Autowired
	private MeterRegistry registry;
	
	@Override
	public void track(Feature feature) {
		registry.counter("app_feature_usage_total", "feature", feature.getName())
        .increment();
	}
	
}
