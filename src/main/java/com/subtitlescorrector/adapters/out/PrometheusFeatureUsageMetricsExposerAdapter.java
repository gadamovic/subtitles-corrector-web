package com.subtitlescorrector.adapters.out;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class PrometheusFeatureUsageMetricsExposerAdapter implements FeatureUsageMetricsExposerPort {

	private Counter correctionsUploadsCounter;
	private Counter conversionsUploadsCounter;
	
	public PrometheusFeatureUsageMetricsExposerAdapter(MeterRegistry registry) {
		this.correctionsUploadsCounter = Counter.builder("corrections_upload_count")
        .description("Number of subtitle files uploaded for corrections")
        .register(registry);
		
		this.conversionsUploadsCounter = Counter.builder("conversions_upload_count")
		        .description("Number of subtitle files uploaded for conversions")
		        .register(registry);
	}
	
	@Override
	public Counter getCorrectionsUploadsCounter() {
		return correctionsUploadsCounter;
	}
	
	@Override
	public Counter getConversionsUploadsCounter() {
		return conversionsUploadsCounter;
	}
	
}
