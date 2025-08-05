package com.subtitlescorrector.core.service;

public interface CloudStorageRateLimiter {

	boolean subtitleCorrectionAllowedForUser(String userIp);

}