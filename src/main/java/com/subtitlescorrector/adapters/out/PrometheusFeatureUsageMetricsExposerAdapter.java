package com.subtitlescorrector.adapters.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subtitlescorrector.core.domain.metrics.Feature;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;

@Component
public class PrometheusFeatureUsageMetricsExposerAdapter implements FeatureUsageMetricsExposerPort {

	private static final String APP_FEATURE_USAGE_TOTAL = "app_feature_usage_total";
	
	@Autowired
	private MeterRegistry registry;
	
	@Override
	public void track(Feature feature) {
		registry.counter(APP_FEATURE_USAGE_TOTAL, "feature", feature.getName())
        .increment();
	}
	
	@PostConstruct
	public void init() {
		for(Feature feature : Feature.values()) {
			registry.counter(APP_FEATURE_USAGE_TOTAL, "feature", feature.getName())
	        .increment(0);
		}
	}
	
}
