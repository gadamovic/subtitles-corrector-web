package com.subtitlescorrector.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subtitlescorrector.core.domain.deepl.DeepLUsageData;
import com.subtitlescorrector.core.port.DeepLUsageMetricsPort;
import com.subtitlescorrector.core.port.DeeplClientPort;

import jakarta.annotation.PostConstruct;

@Service
public class DeepLUsageMetricsExposerServiceImpl implements DeepLUsageMetricsExposerService {

	@Autowired
	DeepLUsageMetricsPort usageMetrics;
	
	@Autowired
	DeeplClientPort deepLClient;
	
	@Override
	public void reportUsage() {
		DeepLUsageData data = deepLClient.getUsageInfo();
		
		long current = data.getCharacterCount();
		long limit = data.getCharacterLimit();
		
		usageMetrics.updateRemainingCount(limit - current);
		usageMetrics.updatePercentUsed((double) current / (double) limit * 100.0);
	}
	
	@PostConstruct
	public void doReportUsage() {
		reportUsage();
	}
	
}
