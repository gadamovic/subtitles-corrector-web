package com.subtitlescorrector.core.port;

public interface DeepLUsageMetricsPort {
	void updateRemainingCount(long remaining);
	void updatePercentUsed(double percent);
}