package com.subtitlescorrector.adapters.out;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;
import com.subtitlescorrector.core.port.DeepLUsageMetricsPort;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class DeepLUsageMetricsAdapter implements DeepLUsageMetricsPort {

    private final AtomicLong remainingCharactersCount = new AtomicLong(9999);
    private final AtomicReference<Double> usedCharactersPercent = new AtomicReference<>(0.0);

    public DeepLUsageMetricsAdapter(MeterRegistry registry) {
        Gauge.builder("deepl_characters_remaining_count", remainingCharactersCount, AtomicLong::get)
            .description("Remaining DeepL translation characters count")
            .register(registry);
        
        Gauge.builder("deepl_characters_used_percent", usedCharactersPercent, AtomicReference::get)
	        .description("Percentage of used DeepL translation characters out of characters available")
	        .register(registry);
    }

    @Override
	public void updateRemainingCount(long remaining) {
        remainingCharactersCount.set(remaining);
    }
    
    @Override
	public void updatePercentUsed(double percent) {
    	usedCharactersPercent.set(percent);
    }
    
}
