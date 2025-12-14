package com.subtitlescorrector.core.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.port.DeepLUsageMetricsPort;
import com.subtitlescorrector.core.port.DeeplClientPort;

import jakarta.annotation.PostConstruct;

@Service
public class DeepLUsageMetricsExposerServiceImpl implements DeepLUsageMetricsExposerService {

	Logger log = LoggerFactory.getLogger(DeepLUsageMetricsExposerServiceImpl.class);

	@Autowired
	DeepLUsageMetricsPort usageMetrics;
	
	@Autowired
	DeeplClientPort deepLClient;
	
	@Autowired
	ScheduledExecutorService scheduledExecutorService;
	
	@Override
	public void reportUsage() {
		
		log.info("Refreshing deepl usage data...");
		DeepLUsageData data = deepLClient.getUsageInfo();
		
		long current = data.getCharacterCount();
		long limit = data.getCharacterLimit();
		
		usageMetrics.updateRemainingCount(limit - current);
		usageMetrics.updatePercentUsed((double) current / (double) limit * 100.0);
	}
	
	@PostConstruct
	public void doReportUsage() {
		scheduledExecutorService.scheduleAtFixedRate(() -> reportUsage(), 5, 60, TimeUnit.MINUTES);
	}
	
}
